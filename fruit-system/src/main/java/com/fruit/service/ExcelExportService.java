package com.fruit.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.dto.excel.*;
import com.fruit.entity.*;
import com.fruit.mapper.*;
import com.fruit.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final CustomerMapper customerMapper;
    private final InboundOrderMapper inboundOrderMapper;
    private final OutboundOrderMapper outboundOrderMapper;
    private final ProductMapper productMapper;
    private final DebtMapper debtMapper;
    private final StatisticsService statisticsService;
    private final JwtUtil jwtUtil;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void exportCustomers(Long userId, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getUserId, userId);
        List<Customer> customers = customerMapper.selectList(wrapper);

        List<CustomerExcelVO> voList = customers.stream().map(customer -> {
            CustomerExcelVO vo = new CustomerExcelVO();
            vo.setName(customer.getName());
            vo.setPhone(customer.getPhone());
            vo.setAddress(customer.getAddress());
            vo.setCategory(getCategoryName(customer.getCategory()));
            vo.setRemark(customer.getRemark());
            vo.setCreateTime(customer.getCreateTime() != null ? sdf.format(customer.getCreateTime()) : "");
            return vo;
        }).collect(Collectors.toList());

        exportExcel(response, voList, CustomerExcelVO.class, "客户数据_" + System.currentTimeMillis());
    }

    public void exportInboundOrders(Long userId, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InboundOrder::getUserId, userId);
        wrapper.orderByDesc(InboundOrder::getCreateTime);
        List<InboundOrder> orders = inboundOrderMapper.selectList(wrapper);

        List<InboundOrderExcelVO> voList = orders.stream().map(order -> {
            InboundOrderExcelVO vo = new InboundOrderExcelVO();
            vo.setOrderNo(order.getOrderNo());
            Product product = productMapper.selectById(order.getProductId());
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setVariety(product.getVariety());
                vo.setGrade(product.getGrade());
            }
            vo.setWeight(order.getWeight());
            vo.setUnitPrice(order.getUnitPrice());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setOrigin(order.getOrigin());
            vo.setRemark(order.getRemark());
            vo.setCreateTime(order.getCreateTime() != null ? sdf.format(order.getCreateTime()) : "");
            return vo;
        }).collect(Collectors.toList());

        exportExcel(response, voList, InboundOrderExcelVO.class, "入库单数据_" + System.currentTimeMillis());
    }

    public void exportOutboundOrders(Long userId, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundOrder::getUserId, userId);
        wrapper.orderByDesc(OutboundOrder::getCreateTime);
        List<OutboundOrder> orders = outboundOrderMapper.selectList(wrapper);

        List<OutboundOrderExcelVO> voList = orders.stream().map(order -> {
            OutboundOrderExcelVO vo = new OutboundOrderExcelVO();
            vo.setOrderNo(order.getOrderNo());
            Customer customer = customerMapper.selectById(order.getCustomerId());
            if (customer != null) {
                vo.setCustomerName(customer.getName());
            }
            Product product = productMapper.selectById(order.getProductId());
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setVariety(product.getVariety());
                vo.setGrade(product.getGrade());
            }
            vo.setWeight(order.getWeight());
            vo.setUnitPrice(order.getUnitPrice());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setCostAmount(order.getCostAmount());
            vo.setProfit(order.getProfit());
            vo.setPaymentType(getPaymentTypeName(order.getPaymentType()));
            vo.setRemark(order.getRemark());
            vo.setCreateTime(order.getCreateTime() != null ? sdf.format(order.getCreateTime()) : "");
            return vo;
        }).collect(Collectors.toList());

        exportExcel(response, voList, OutboundOrderExcelVO.class, "出库单数据_" + System.currentTimeMillis());
    }

    public void exportStatistics(Long userId, HttpServletResponse response) throws IOException {
        List<StatisticsExcelVO> voList = new ArrayList<>();

        com.fruit.dto.resp.StatisticsResp stats = statisticsService.getStatisticsByUserId(userId);
        StatisticsExcelVO todayVo = new StatisticsExcelVO();
        todayVo.setType("今日统计");
        todayVo.setInboundAmount(stats.getTodayInboundAmount());
        todayVo.setOutboundAmount(stats.getTodayOutboundAmount());
        todayVo.setProfit(stats.getMonthProfit());
        todayVo.setPendingDebt(stats.getTotalUnpaidAmount());
        todayVo.setStatisticsTime(sdf.format(new java.util.Date()));
        voList.add(todayVo);

        StatisticsExcelVO monthVo = new StatisticsExcelVO();
        monthVo.setType("本月统计");
        monthVo.setInboundAmount(stats.getMonthInboundAmount());
        monthVo.setOutboundAmount(stats.getMonthOutboundAmount());
        monthVo.setProfit(stats.getMonthProfit());
        monthVo.setPendingDebt(stats.getTotalUnpaidAmount());
        monthVo.setStatisticsTime(sdf.format(new java.util.Date()));
        voList.add(monthVo);

        exportExcel(response, voList, StatisticsExcelVO.class, "统计数据_" + System.currentTimeMillis());
    }

    private <T> void exportExcel(HttpServletResponse response, List<T> data, Class<T> clazz, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), clazz).sheet("Sheet1").doWrite(data);
    }

    private String getCategoryName(Integer category) {
        if (category == null) return "";
        switch (category) {
            case 1: return "优质客户";
            case 2: return "一般客户";
            case 3: return "欠款客户";
            default: return "";
        }
    }

    private String getPaymentTypeName(Integer paymentType) {
        if (paymentType == null) return "";
        switch (paymentType) {
            case 1: return "现款";
            case 2: return "欠款";
            default: return "";
        }
    }
}
