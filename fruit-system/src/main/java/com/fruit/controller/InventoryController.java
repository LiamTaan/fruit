package com.fruit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.Result;
import com.fruit.dto.resp.InventoryResp;
import com.fruit.service.InventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "库存管理", description = "库存的查询")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @ApiOperation("分页查询库存列表")
    @GetMapping({"/page", "/list"})
    public Result<Page<InventoryResp>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<InventoryResp> page = inventoryService.page(pageNum, pageSize, keyword);
        return Result.success(page);
    }

    @ApiOperation("获取库存预警列表")
    @GetMapping("/warning")
    public Result<List<InventoryResp>> getWarningList(@RequestParam(required = false) String keyword) {
        List<InventoryResp> list = inventoryService.getWarningList(keyword);
        return Result.success(list);
    }

    @ApiOperation("获取库存详情")
    @GetMapping("/{id}")
    public Result<InventoryResp> getById(@PathVariable Long id) {
        InventoryResp resp = inventoryService.getById(id);
        return Result.success(resp);
    }
}
