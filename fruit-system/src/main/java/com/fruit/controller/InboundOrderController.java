package com.fruit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.Result;
import com.fruit.dto.req.InboundOrderReq;
import com.fruit.dto.resp.InboundOrderResp;
import com.fruit.service.InboundOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "入库管理", description = "入库单的增删改查")
@RestController
@RequestMapping("/api/inbound")
@RequiredArgsConstructor
public class InboundOrderController {

    private final InboundOrderService inboundOrderService;

    @ApiOperation("分页查询入库单列表")
    @GetMapping({"/page", "/list"})
    public Result<Page<InboundOrderResp>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<InboundOrderResp> page = inboundOrderService.page(pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("获取入库单详情")
    @GetMapping("/{id}")
    public Result<InboundOrderResp> getById(@PathVariable Long id) {
        InboundOrderResp resp = inboundOrderService.getById(id);
        return Result.success(resp);
    }

    @ApiOperation("新增入库单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody InboundOrderReq req) {
        inboundOrderService.create(req);
        return Result.success();
    }
}
