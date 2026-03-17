package com.fruit.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProductReq {
    private Long id;

    @NotBlank(message = "果品名称不能为空")
    private String name;

    private String variety;
    private String grade;
    private String unit;
    private Boolean disabled;
}
