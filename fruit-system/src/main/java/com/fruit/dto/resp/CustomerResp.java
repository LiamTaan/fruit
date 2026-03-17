package com.fruit.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResp {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private String remark;
    private Integer category;
    private String categoryName;
    private String avatar;
    private BigDecimal totalAmount;
    private BigDecimal debtAmount;
    private BigDecimal totalDebtAmount;
    private LocalDateTime createTime;
    // 前端使用createdAt，添加别名
    private LocalDateTime createdAt;
}
