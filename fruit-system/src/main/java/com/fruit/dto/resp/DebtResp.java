package com.fruit.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebtResp {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long outboundOrderId;
    private String outboundOrderNo;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private Integer status;
    private String statusName;
    private String remark;
    private LocalDateTime dueDate;
    private LocalDateTime createTime;
}
