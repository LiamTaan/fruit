package com.fruit.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.exception.BusinessException;
import com.fruit.dto.req.OutboundOrderReq;
import com.fruit.dto.resp.OutboundOrderResp;
import com.fruit.entity.*;
import com.fruit.mapper.*;
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
public class OutboundOrderService {

    private final OutboundOrderMapper outboundOrderMapper;
    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final DebtMapper debtMapper;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<OutboundOrderResp> page(int pageNum, int pageSize, Integer isDebt) {
        Long userId = getCurrentUserId();
        Page<OutboundOrder> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundOrder::getUserId, userId);
        
        // 根据isDebt参数过滤，1-现款，2-欠款
        if (isDebt != null) {
            wrapper.eq(OutboundOrder::getPaymentType, isDebt + 1);
        }
        
        wrapper.orderByDesc(OutboundOrder::getCreateTime);
        
        Page<OutboundOrder> resultPage = outboundOrderMapper.selectPage(page, wrapper);
        
        Page<OutboundOrderResp> respPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<OutboundOrderResp> respList = resultPage.getRecords().stream()
            .map(this::convertToResp)
            .collect(Collectors.toList());
        respPage.setRecords(respList);
        
        return respPage;
    }
    
    public Page<OutboundOrderResp> pageByCustomerId(int pageNum, int pageSize, Long customerId) {
        Long userId = getCurrentUserId();
        Page<OutboundOrder> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundOrder::getUserId, userId);
        wrapper.eq(OutboundOrder::getCustomerId, customerId);
        wrapper.orderByDesc(OutboundOrder::getCreateTime);
        
        Page<OutboundOrder> resultPage = outboundOrderMapper.selectPage(page, wrapper);
        
        Page<OutboundOrderResp> respPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<OutboundOrderResp> respList = resultPage.getRecords().stream()
            .map(this::convertToResp)
            .collect(Collectors.toList());
        respPage.setRecords(respList);
        
        return respPage;
    }

    public OutboundOrderResp getById(Long id) {
        OutboundOrder order = outboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }
        return convertToResp(order);
    }

    @Transactional
    public void create(OutboundOrderReq req) {
        Long userId = getCurrentUserId();
        Long operatorId = userId;
        
        Customer customer = customerMapper.selectById(req.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        
        Product product = productMapper.selectById(req.getProductId());
        if (product == null) {
            throw new BusinessException("果品不存在");
        }
        
        LambdaQueryWrapper<Inventory> invWrapper = new LambdaQueryWrapper<>();
        invWrapper.eq(Inventory::getProductId, product.getId());
        invWrapper.eq(Inventory::getUserId, userId);
        Inventory inventory = inventoryMapper.selectOne(invWrapper);
        if (inventory == null || inventory.getQuantity().compareTo(req.getWeight()) < 0) {
            throw new BusinessException("库存不足");
        }
        
        BigDecimal totalAmount = req.getWeight().multiply(req.getUnitPrice());
        BigDecimal costAmount = req.getWeight().multiply(inventory.getCostPrice());
        BigDecimal profit = totalAmount.subtract(costAmount);
        
        OutboundOrder order = new OutboundOrder();
        BeanUtils.copyProperties(req, order);
        order.setOrderNo(generateOrderNo());
        order.setTotalAmount(totalAmount);
        order.setCostAmount(costAmount);
        order.setProfit(profit);
        order.setOperatorId(operatorId);
        order.setUserId(userId);
        // 设置当前时间为出库时间
        if (order.getOutboundTime() == null) {
            order.setOutboundTime(LocalDateTime.now());
        }
        outboundOrderMapper.insert(order);
        
        inventory.setQuantity(inventory.getQuantity().subtract(req.getWeight()));
        inventoryMapper.updateById(inventory);
        
        if (req.getPaymentType() == 2) {
            createDebt(order, customer, userId);
        }
    }

    private void createDebt(OutboundOrder order, Customer customer, Long userId) {
        Debt debt = new Debt();
        debt.setCustomerId(customer.getId());
        debt.setOutboundOrderId(order.getId());
        debt.setTotalAmount(order.getTotalAmount());
        debt.setPaidAmount(BigDecimal.ZERO);
        debt.setRemainingAmount(order.getTotalAmount());
        debt.setStatus(1);
        debt.setUserId(userId);
        // 设置当前时间为欠款时间（到期日期）
        debt.setDueDate(LocalDateTime.now());
        debtMapper.insert(debt);
    }

    private String generateOrderNo() {
        return "CK" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 4).toUpperCase();
    }

    private OutboundOrderResp convertToResp(OutboundOrder order) {
        OutboundOrderResp resp = new OutboundOrderResp();
        BeanUtils.copyProperties(order, resp);
        resp.setPaymentTypeName(getPaymentTypeName(order.getPaymentType()));
        
        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer != null) {
            resp.setCustomerName(customer.getName());
        }
        
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            resp.setProductName(product.getName());
            resp.setVariety(product.getVariety());
            resp.setGrade(product.getGrade());
            resp.setUnit(product.getUnit());
        }
        
        // 计算成本单价
        if (order.getCostAmount() != null && order.getWeight() != null && order.getWeight().compareTo(BigDecimal.ZERO) > 0) {
            // 成本单价 = 成本金额 / 重量
            resp.setCostPrice(order.getCostAmount().divide(order.getWeight(), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            // 从库存中获取成本单价
            LambdaQueryWrapper<Inventory> invWrapper = new LambdaQueryWrapper<>();
            invWrapper.eq(Inventory::getProductId, order.getProductId());
            invWrapper.eq(Inventory::getUserId, order.getUserId());
            Inventory inventory = inventoryMapper.selectOne(invWrapper);
            if (inventory != null) {
                resp.setCostPrice(inventory.getCostPrice());
            }
        }
        
        return resp;
    }

    private String getPaymentTypeName(Integer paymentType) {
        if (paymentType == null) {
            return "未知";
        }
        switch (paymentType) {
            case 1:
                return "现款";
            case 2:
                return "欠款";
            default:
                return "未知";
        }
    }
}
