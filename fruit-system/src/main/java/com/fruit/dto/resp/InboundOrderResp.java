package com.fruit.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InboundOrderResp {
    private Long id;
    private String orderNo;
    private Long productId;
    private String productName;
    private String variety;
    private String grade;
    private String unit;
    private BigDecimal weight;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String origin;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime inboundTime;
}
