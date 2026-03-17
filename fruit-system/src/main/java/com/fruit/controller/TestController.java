package com.fruit.controller;

import com.fruit.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试", description = "测试接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @ApiOperation("测试接口")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, 运城果业！");
    }
}
