package com.fruit.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResp {
    private Long id;
    private String name;
    private String variety;
    private String grade;
    private String unit;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean disabled;
    private BigDecimal stock;
}
