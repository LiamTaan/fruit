package com.fruit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_debt")
public class Debt extends BaseEntity {
    private Long customerId;
    private Long outboundOrderId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private Integer status;
    private String remark;
    private LocalDateTime dueDate;
    private Long userId;
}
