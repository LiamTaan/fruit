package com.fruit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.Result;
import com.fruit.dto.req.OutboundOrderReq;
import com.fruit.dto.resp.OutboundOrderResp;
import com.fruit.service.OutboundOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "出库管理", description = "出库单的增删改查")
@RestController
@RequestMapping("/api/outbound")
@RequiredArgsConstructor
public class OutboundOrderController {

    private final OutboundOrderService outboundOrderService;

    @ApiOperation("分页查询出库单列表")
    @GetMapping({"/page", "/list"})
    public Result<Page<OutboundOrderResp>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer isDebt) {
        Page<OutboundOrderResp> page = outboundOrderService.page(pageNum, pageSize, isDebt);
        return Result.success(page);
    }
    
    @ApiOperation("根据客户ID分页查询出库单列表")
    @GetMapping("/customer/{customerId}/list")
    public Result<Page<OutboundOrderResp>> pageByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<OutboundOrderResp> page = outboundOrderService.pageByCustomerId(pageNum, pageSize, customerId);
        return Result.success(page);
    }

    @ApiOperation("获取出库单详情")
    @GetMapping("/{id}")
    public Result<OutboundOrderResp> getById(@PathVariable Long id) {
        OutboundOrderResp resp = outboundOrderService.getById(id);
        return Result.success(resp);
    }

    @ApiOperation("新增出库单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody OutboundOrderReq req) {
        outboundOrderService.create(req);
        return Result.success();
    }
}
