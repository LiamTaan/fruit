package com.fruit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product")
public class Product extends BaseEntity {
    private String name;
    private String variety;
    private String grade;
    private String unit;
    private Long userId;
    private Boolean disabled;
}
