package com.fruit.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("修改密码请求")
public class ChangePasswordReq {

    @ApiModelProperty("旧密码")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @ApiModelProperty("新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
