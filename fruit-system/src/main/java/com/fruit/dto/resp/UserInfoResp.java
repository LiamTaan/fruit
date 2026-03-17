package com.fruit.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResp {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private Integer role;
    private Integer status;
    private Long parentId;
}
