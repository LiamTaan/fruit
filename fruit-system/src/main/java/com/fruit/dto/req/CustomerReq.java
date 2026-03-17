package com.fruit.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomerReq {
    private Long id;

    @NotBlank(message = "客户姓名不能为空")
    private String name;

    private String phone;
    private String address;
    private String remark;
    private Integer category;
    private String avatar;
}
