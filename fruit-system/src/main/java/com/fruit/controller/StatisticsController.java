package com.fruit.controller;

import com.fruit.common.Result;
import com.fruit.dto.resp.StatisticsResp;
import com.fruit.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "统计分析", description = "数据统计")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @ApiOperation("获取统计数据")
    @GetMapping
    public Result<StatisticsResp> getStatistics(
            @RequestParam(defaultValue = "month") String period) {
        StatisticsResp resp = statisticsService.getStatistics(period);
        return Result.success(resp);
    }
}
