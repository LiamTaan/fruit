package com.fruit.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboundOrderResp {
    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private String variety;
    private String grade;
    private String unit;
    private BigDecimal weight;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private BigDecimal totalAmount;
    private BigDecimal profit;
    private Integer paymentType;
    private String paymentTypeName;
    private Long operatorId;
    private String operatorName;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime outboundTime;
}
