package com.fruit.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResp {
    // 今日数据
    private BigDecimal todayInboundAmount;
    private BigDecimal todayOutboundAmount;
    private BigDecimal todayProfit;
    private BigDecimal todayDebtAmount;
    private Integer todayInboundCount;
    private Integer todayOutboundCount;
    private Integer todayDebtCount;
    
    // 本月数据
    private BigDecimal monthInboundAmount;
    private BigDecimal monthOutboundAmount;
    private BigDecimal monthProfit;
    private BigDecimal monthDebtAmount;
    private Integer monthInboundCount;
    private Integer monthOutboundCount;
    private Integer monthDebtCount;
    
    // 全年数据
    private BigDecimal yearInboundAmount;
    private BigDecimal yearOutboundAmount;
    private BigDecimal yearProfit;
    private BigDecimal yearDebtAmount;
    private Integer yearInboundCount;
    private Integer yearOutboundCount;
    private Integer yearDebtCount;
    
    // 总欠款
    private BigDecimal totalUnpaidDebt;
    private BigDecimal totalUnpaidAmount;
    
    // 商品销量排行
    private List<ProductSaleRank> productSaleRanks;
    
    // 商品销量排行内部类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSaleRank {
        private Long productId;
        private String productName;
        private String variety;
        private String grade;
        private String unit;
        private BigDecimal totalWeight;
        private BigDecimal totalAmount;
    }
}
