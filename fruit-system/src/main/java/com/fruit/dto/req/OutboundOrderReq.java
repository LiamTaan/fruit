package com.fruit.dto.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OutboundOrderReq {
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotNull(message = "果品ID不能为空")
    private Long productId;

    @NotNull(message = "重量不能为空")
    private BigDecimal weight;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    @NotNull(message = "收款方式不能为空")
    private Integer paymentType;

    private String remark;
}
