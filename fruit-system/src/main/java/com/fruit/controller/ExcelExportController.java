package com.fruit.controller;

import com.fruit.service.ExcelExportService;
import com.fruit.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags = "Excel导出", description = "导出客户、入库单、出库单、统计数据")
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExcelExportController {

    private final ExcelExportService excelExportService;
    private final JwtUtil jwtUtil;

    @ApiOperation("导出客户数据")
    @GetMapping("/customers")
    public void exportCustomers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        excelExportService.exportCustomers(userId, response);
    }

    @ApiOperation("导出入库单数据")
    @GetMapping("/inbound-orders")
    public void exportInboundOrders(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        excelExportService.exportInboundOrders(userId, response);
    }

    @ApiOperation("导出出库单数据")
    @GetMapping("/outbound-orders")
    public void exportOutboundOrders(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        excelExportService.exportOutboundOrders(userId, response);
    }

    @ApiOperation("导出统计数据")
    @GetMapping("/statistics")
    public void exportStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        excelExportService.exportStatistics(userId, response);
    }
}
