package com.fruit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.Result;
import com.fruit.dto.req.CustomerReq;
import com.fruit.dto.resp.CustomerResp;
import com.fruit.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "客户管理", description = "客户的增删改查")
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @ApiOperation("分页查询客户列表")
    @GetMapping({"/page", "/list"})
    public Result<Page<CustomerResp>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer category) {
        Page<CustomerResp> page = customerService.page(pageNum, pageSize, keyword, category);
        return Result.success(page);
    }

    @ApiOperation("获取客户详情")
    @GetMapping("/{id}")
    public Result<CustomerResp> getById(@PathVariable Long id) {
        CustomerResp resp = customerService.getById(id);
        return Result.success(resp);
    }

    @ApiOperation("新增客户")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CustomerReq req) {
        customerService.create(req);
        return Result.success();
    }

    @ApiOperation("更新客户")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody CustomerReq req) {
        customerService.update(req);
        return Result.success();
    }

    @ApiOperation("删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success();
    }
}
