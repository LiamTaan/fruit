package com.fruit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.Result;
import com.fruit.dto.req.UserCreateReq;
import com.fruit.dto.req.UserUpdateReq;
import com.fruit.dto.resp.UserInfoResp;
import com.fruit.service.UserService;
import com.fruit.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "用户管理", description = "用户列表查询、用户详情、创建用户、更新用户、删除用户")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @ApiOperation("分页查询用户列表")
    @GetMapping("/page")
    public Result<IPage<UserInfoResp>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        IPage<UserInfoResp> page = userService.page(pageNum, pageSize, keyword);
        return Result.success(page);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public Result<UserInfoResp> getById(@PathVariable Long id) {
        UserInfoResp resp = userService.getById(id);
        return Result.success(resp);
    }

    @ApiOperation("创建用户")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserCreateReq req) {
        userService.create(req);
        return Result.success();
    }

    @ApiOperation("更新用户")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserUpdateReq req) {
        userService.update(req);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}