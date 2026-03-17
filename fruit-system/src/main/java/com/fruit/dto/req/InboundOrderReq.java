package com.fruit.dto.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class InboundOrderReq {
    @NotNull(message = "果品ID不能为空")
    private Long productId;

    @NotNull(message = "重量不能为空")
    private BigDecimal weight;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    // 库存预警阈值
    private BigDecimal warningThreshold;

    private String origin;
    private String remark;
}
