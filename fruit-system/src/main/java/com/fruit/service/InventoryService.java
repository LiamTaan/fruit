package com.fruit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.dto.resp.InventoryResp;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryMapper inventoryMapper;
    private final ProductMapper productMapper;
    private final InboundOrderMapper inboundOrderMapper;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<InventoryResp> page(int pageNum, int pageSize, String keyword) {
        Long userId = getCurrentUserId();
        Page<Inventory> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getUserId, userId);
        wrapper.orderByDesc(Inventory::getUpdateTime);
        
        Page<Inventory> resultPage = inventoryMapper.selectPage(page, wrapper);
        
        Page<InventoryResp> respPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<InventoryResp> respList = resultPage.getRecords().stream()
            .map(this::convertToResp)
            .filter(resp -> {
                // 筛选关键词
                if (keyword == null || keyword.trim().isEmpty()) {
                    return true;
                }
                String searchTerm = keyword.toLowerCase().trim();
                // 确保productName不为null，避免空指针异常
                String productName = resp.getProductName() != null ? resp.getProductName().toLowerCase() : "";
                String variety = resp.getVariety() != null ? resp.getVariety().toLowerCase() : "";
                String grade = resp.getGrade() != null ? resp.getGrade().toLowerCase() : "";
                return productName.contains(searchTerm) || variety.contains(searchTerm) || grade.contains(searchTerm);
            })
            .collect(Collectors.toList());
        respPage.setRecords(respList);
        
        return respPage;
    }

    public List<InventoryResp> getWarningList(String keyword) {
        Long userId = getCurrentUserId();
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getUserId, userId);
        wrapper.isNotNull(Inventory::getWarningThreshold);
        wrapper.apply("quantity <= warning_threshold");
        wrapper.orderByDesc(Inventory::getUpdateTime);
        
        return inventoryMapper.selectList(wrapper).stream()
            .map(this::convertToResp)
            .filter(resp -> {
                // 筛选关键词
                if (keyword == null || keyword.trim().isEmpty()) {
                    return true;
                }
                String searchTerm = keyword.toLowerCase().trim();
                // 确保productName不为null，避免空指针异常
                String productName = resp.getProductName() != null ? resp.getProductName().toLowerCase() : "";
                String variety = resp.getVariety() != null ? resp.getVariety().toLowerCase() : "";
                String grade = resp.getGrade() != null ? resp.getGrade().toLowerCase() : "";
                return productName.contains(searchTerm) || variety.contains(searchTerm) || grade.contains(searchTerm);
            })
            .collect(Collectors.toList());
    }

    public InventoryResp getById(Long id) {
        Inventory inventory = inventoryMapper.selectById(id);
        return convertToResp(inventory);
    }

    private InventoryResp convertToResp(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        InventoryResp resp = new InventoryResp();
        BeanUtils.copyProperties(inventory, resp);
        
        Product product = productMapper.selectById(inventory.getProductId());
        if (product != null) {
            resp.setProductName(product.getName());
            resp.setVariety(product.getVariety());
            resp.setGrade(product.getGrade());
            resp.setUnit(product.getUnit());
        }
        
        // 基于历史入库记录重新计算平均成本
        LambdaQueryWrapper<InboundOrder> inboundWrapper = new LambdaQueryWrapper<>();
        inboundWrapper.eq(InboundOrder::getProductId, inventory.getProductId());
        inboundWrapper.eq(InboundOrder::getUserId, inventory.getUserId());
        List<InboundOrder> inboundOrders = inboundOrderMapper.selectList(inboundWrapper);
        
        // 计算总入库金额和总入库重量
        BigDecimal totalInboundAmount = inboundOrders.stream()
            .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalInboundWeight = inboundOrders.stream()
            .map(order -> order.getWeight() != null ? order.getWeight() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算平均成本和总成本
        BigDecimal avgCostPrice;
        if (totalInboundWeight.compareTo(BigDecimal.ZERO) > 0) {
            // 基于历史入库记录计算平均成本
            avgCostPrice = totalInboundAmount.divide(totalInboundWeight, 2, BigDecimal.ROUND_HALF_UP);
        } else {
            // 如果没有入库记录，使用库存中的成本单价，确保不为null
            avgCostPrice = inventory.getCostPrice() != null ? inventory.getCostPrice() : BigDecimal.ZERO;
        }
        
        // 确保quantity不为null
        BigDecimal quantity = inventory.getQuantity() != null ? inventory.getQuantity() : BigDecimal.ZERO;
        
        resp.setAvgCostPrice(avgCostPrice);
        resp.setTotalCost(quantity.multiply(avgCostPrice));
        resp.setTotalInboundCost(totalInboundAmount);
        
        if (inventory.getWarningThreshold() != null) {
            resp.setIsWarning(inventory.getQuantity().compareTo(inventory.getWarningThreshold()) <= 0);
        } else {
            resp.setIsWarning(false);
        }
        
        return resp;
    }
}
