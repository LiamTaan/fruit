package com.fruit.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.exception.BusinessException;
import com.fruit.dto.req.InboundOrderReq;
import com.fruit.dto.resp.InboundOrderResp;
import com.fruit.entity.InboundOrder;
import com.fruit.entity.Inventory;
import com.fruit.entity.Product;
import com.fruit.entity.User;
import com.fruit.mapper.InboundOrderMapper;
import com.fruit.mapper.InventoryMapper;
import com.fruit.mapper.ProductMapper;
import com.fruit.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InboundOrderService {

    private final InboundOrderMapper inboundOrderMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final UserMapper userMapper;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<InboundOrderResp> page(int pageNum, int pageSize) {
        Long userId = getCurrentUserId();
        Page<InboundOrder> page = new Page<>(pageNum, pageSize);
        
        // 获取当前用户信息
        User currentUser = userMapper.selectById(userId);
        
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 实现业务数据的隔离：
        // - 管理员可以看到自己和下属员工创建的入库单
        // - 员工只能看到自己创建的入库单
        if (currentUser.getRole() == 1) { // 管理员
            // 查询管理员自己和下属员工的ID列表
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.and(w -> w.eq(User::getId, userId) // 管理员自己
                               .or().eq(User::getParentId, userId)); // 下属员工
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            
            wrapper.in(InboundOrder::getUserId, userIds);
        } else { // 员工
            wrapper.eq(InboundOrder::getUserId, userId); // 只能看到自己创建的入库单
        }
        
        wrapper.orderByDesc(InboundOrder::getCreateTime);
        
        Page<InboundOrder> resultPage = inboundOrderMapper.selectPage(page, wrapper);
        
        Page<InboundOrderResp> respPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<InboundOrderResp> respList = resultPage.getRecords().stream()
            .map(this::convertToResp)
            .collect(Collectors.toList());
        respPage.setRecords(respList);
        
        return respPage;
    }

    public InboundOrderResp getById(Long id) {
        InboundOrder order = inboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }
        
        // 数据安全检查
        Long userId = getCurrentUserId();
        User currentUser = userMapper.selectById(userId);
        
        if (currentUser.getRole() == 1) { // 管理员
            // 查询管理员自己和下属员工的ID列表
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.and(w -> w.eq(User::getId, userId) // 管理员自己
                               .or().eq(User::getParentId, userId)); // 下属员工
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            
            if (!userIds.contains(order.getUserId())) {
                throw new BusinessException("无权查看该入库单");
            }
        } else { // 员工
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException("无权查看该入库单");
            }
        }
        
        return convertToResp(order);
    }

    @Transactional
    public void create(InboundOrderReq req) {
        Long userId = getCurrentUserId();
        Long operatorId = userId;
        
        Product product = productMapper.selectById(req.getProductId());
        if (product == null) {
            throw new BusinessException("果品不存在");
        }
        
        BigDecimal totalAmount = req.getWeight().multiply(req.getUnitPrice());
        
        InboundOrder order = new InboundOrder();
        BeanUtils.copyProperties(req, order);
        order.setOrderNo(generateOrderNo());
        order.setTotalAmount(totalAmount);
        order.setOperatorId(operatorId);
        order.setUserId(userId); // 订单创建者是当前管理员
        // 设置当前时间为入库时间
        if (order.getInboundTime() == null) {
            order.setInboundTime(LocalDateTime.now());
        }
        inboundOrderMapper.insert(order);
        
        // 对产品创建者的库存进行入库操作
        updateInventory(product, req.getWeight(), req.getUnitPrice(), req.getWarningThreshold(), product.getUserId());
    }

    private void updateInventory(Product product, BigDecimal weight, BigDecimal unitPrice, BigDecimal warningThreshold, Long userId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getProductId, product.getId());
        wrapper.eq(Inventory::getUserId, userId);
        Inventory inventory = inventoryMapper.selectOne(wrapper);
        
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProductId(product.getId());
            inventory.setQuantity(weight);
            inventory.setCostPrice(unitPrice);
            inventory.setWarningThreshold(warningThreshold);
            inventory.setUserId(userId);
            inventoryMapper.insert(inventory);
        } else {
            BigDecimal totalQuantity = inventory.getQuantity().add(weight);
            BigDecimal totalCost = inventory.getCostPrice().multiply(inventory.getQuantity())
                .add(unitPrice.multiply(weight));
            BigDecimal avgCostPrice = totalCost.divide(totalQuantity, 2, BigDecimal.ROUND_HALF_UP);
            
            inventory.setQuantity(totalQuantity);
            inventory.setCostPrice(avgCostPrice);
            
            // 如果传入了预警阈值，则更新预警阈值
            if (warningThreshold != null) {
                inventory.setWarningThreshold(warningThreshold);
            }
            inventoryMapper.updateById(inventory);
        }
    }

    private String generateOrderNo() {
        return "RK" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 4).toUpperCase();
    }

    private InboundOrderResp convertToResp(InboundOrder order) {
        InboundOrderResp resp = new InboundOrderResp();
        BeanUtils.copyProperties(order, resp);
        
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            resp.setProductName(product.getName());
            resp.setVariety(product.getVariety());
            resp.setGrade(product.getGrade());
            resp.setUnit(product.getUnit());
        }
        
        // 获取操作人昵称
        if (order.getOperatorId() != null) {
            User operator = userMapper.selectById(order.getOperatorId());
            if (operator != null) {
                resp.setOperatorName(operator.getUsername());
            }
        }
        
        return resp;
    }
}
