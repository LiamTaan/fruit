package com.fruit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_inbound_order")
public class InboundOrder extends BaseEntity {
    private String orderNo;
    private Long productId;
    private BigDecimal weight;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String origin;
    private LocalDateTime inboundTime;
    private String remark;
    private Long operatorId;
    private Long userId;
}
