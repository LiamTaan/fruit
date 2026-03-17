package com.fruit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_outbound_order")
public class OutboundOrder extends BaseEntity {
    private String orderNo;
    private Long customerId;
    private Long productId;
    private BigDecimal weight;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private BigDecimal costAmount;
    private BigDecimal profit;
    private LocalDateTime outboundTime;
    private Integer paymentType;
    private String remark;
    private Long operatorId;
    private Long userId;
}
