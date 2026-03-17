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
import com.fruit.mapper.InboundOrderMapper;
import com.fruit.mapper.InventoryMapper;
import com.fruit.mapper.ProductMapper;
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

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<InboundOrderResp> page(int pageNum, int pageSize) {
        Long userId = getCurrentUserId();
        Page<InboundOrder> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InboundOrder::getUserId, userId);
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
        order.setUserId(userId);
        // 设置当前时间为入库时间
        if (order.getInboundTime() == null) {
            order.setInboundTime(LocalDateTime.now());
        }
        inboundOrderMapper.insert(order);
        
        updateInventory(product, req.getWeight(), req.getUnitPrice(), req.getWarningThreshold(), userId);
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
        
        return resp;
    }
}
