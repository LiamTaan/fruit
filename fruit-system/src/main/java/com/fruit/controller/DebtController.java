package com.fruit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.Result;
import com.fruit.dto.req.RepaymentReq;
import com.fruit.dto.resp.DebtResp;
import com.fruit.service.DebtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "欠款管理", description = "欠款的查询和还款")
@RestController
@RequestMapping("/api/debt")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;

    @ApiOperation("分页查询欠款列表")
    @GetMapping({"/page", "/list"})
    public Result<Page<DebtResp>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<DebtResp> page = debtService.page(pageNum, pageSize, status, keyword);
        return Result.success(page);
    }

    @ApiOperation("获取欠款详情")
    @GetMapping("/{id}")
    public Result<DebtResp> getById(@PathVariable Long id) {
        DebtResp resp = debtService.getById(id);
        return Result.success(resp);
    }

    @ApiOperation("还款")
    @PostMapping("/repayment")
    public Result<Void> repayment(@Valid @RequestBody RepaymentReq req) {
        debtService.repayment(req);
        return Result.success();
    }
}
