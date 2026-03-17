package com.fruit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer")
public class Customer extends BaseEntity {
    private String name;
    private String phone;
    private String address;
    private String remark;
    private Integer category;
    private String avatar;
    private Long userId;
}
