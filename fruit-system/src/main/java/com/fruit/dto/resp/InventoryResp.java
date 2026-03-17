package com.fruit.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResp {
    private Long id;
    private Long productId;
    private String productName;
    private String variety;
    private String grade;
    private BigDecimal quantity;
    private BigDecimal costPrice;
    // 平均成本（基于历史入库记录）
    private BigDecimal avgCostPrice;
    // 总成本（当前库存 * 平均成本）
    private BigDecimal totalCost;
    // 总入库成本（基于历史入库记录）
    private BigDecimal totalInboundCost;
    private BigDecimal warningThreshold;
    private Boolean isWarning;
    private LocalDateTime updateTime;
    private String unit;
}
