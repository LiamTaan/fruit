package com.fruit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_inventory")
public class Inventory extends BaseEntity {
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal costPrice;
    private BigDecimal warningThreshold;
    private Long userId;
}
