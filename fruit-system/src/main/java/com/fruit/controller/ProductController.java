package com.fruit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.Result;
import com.fruit.dto.req.ProductReq;
import com.fruit.dto.resp.ProductResp;
import com.fruit.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "果品管理", description = "果品的增删改查")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ApiOperation("分页查询果品列表")
    @GetMapping({"/page", "/list"})
    public Result<Page<ProductResp>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<ProductResp> page = productService.page(pageNum, pageSize, keyword);
        return Result.success(page);
    }

    @ApiOperation("获取所有果品")
    @GetMapping("/all")
    public Result<List<ProductResp>> listAll() {
        List<ProductResp> list = productService.listAll();
        return Result.success(list);
    }

    @ApiOperation("获取果品详情")
    @GetMapping("/{id}")
    public Result<ProductResp> getById(@PathVariable Long id) {
        ProductResp resp = productService.getById(id);
        return Result.success(resp);
    }

    @ApiOperation("新增果品")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ProductReq req) {
        productService.create(req);
        return Result.success();
    }

    @ApiOperation("更新果品")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ProductReq req) {
        productService.update(req);
        return Result.success();
    }

    @ApiOperation("删除果品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }
    
    @ApiOperation("启用/禁用果品")
    @PutMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id, @RequestParam Boolean disabled) {
        productService.disable(id, disabled);
        return Result.success();
    }
}
