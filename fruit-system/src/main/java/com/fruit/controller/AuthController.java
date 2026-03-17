package com.fruit.controller;

import com.fruit.common.Result;
import com.fruit.dto.req.ChangePasswordReq;
import com.fruit.dto.req.LoginReq;
import com.fruit.dto.resp.LoginResp;
import com.fruit.dto.resp.UserInfoResp;
import com.fruit.service.UserService;
import com.fruit.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "用户认证", description = "用户登录、登出、获取用户信息、修改密码")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginResp> login(@Valid @RequestBody LoginReq req) {
        LoginResp resp = userService.login(req);
        return Result.success(resp);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/userinfo")
    public Result<UserInfoResp> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserInfoResp userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @ApiOperation("修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordReq req, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.changePassword(userId, req);
        return Result.success();
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
