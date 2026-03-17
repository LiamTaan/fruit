package com.fruit.dto.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RepaymentReq {
    @NotNull(message = "欠款ID不能为空")
    private Long debtId;

    @NotNull(message = "还款金额不能为空")
    private BigDecimal amount;

    private String remark;
}
