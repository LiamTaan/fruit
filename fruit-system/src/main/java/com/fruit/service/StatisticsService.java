package com.fruit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.dto.resp.StatisticsResp;
import com.fruit.entity.Debt;
import com.fruit.entity.InboundOrder;
import com.fruit.entity.OutboundOrder;
import com.fruit.entity.Product;
import com.fruit.mapper.DebtMapper;
import com.fruit.mapper.InboundOrderMapper;
import com.fruit.mapper.OutboundOrderMapper;
import com.fruit.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final InboundOrderMapper inboundOrderMapper;
    private final OutboundOrderMapper outboundOrderMapper;
    private final DebtMapper debtMapper;
    private final ProductMapper productMapper;

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Current user principal: " + principal);
        return (Long) principal;
    }

    public StatisticsResp getStatistics(String period) {
        Long userId = getCurrentUserId();
        return getStatisticsByUserId(userId, period);
    }

    public StatisticsResp getStatisticsByUserId(Long userId) {
        return getStatisticsByUserId(userId, "month");
    }

    public StatisticsResp getStatisticsByUserId(Long userId, String period) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);
        
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime monthStart = firstDayOfMonth.atStartOfDay();
        LocalDateTime monthEnd = today.atTime(LocalTime.MAX);
        
        LocalDate firstDayOfYear = today.withDayOfYear(1);
        LocalDateTime yearStart = firstDayOfYear.atStartOfDay();
        LocalDateTime yearEnd = today.atTime(LocalTime.MAX);
        
        // 计算今日数据
        BigDecimal todayInboundAmount = calculateInboundAmount(userId, todayStart, todayEnd);
        BigDecimal todayOutboundAmount = calculateOutboundAmount(userId, todayStart, todayEnd);
        BigDecimal todayProfit = calculateProfit(userId, todayStart, todayEnd);
        BigDecimal todayDebtAmount = calculateDebtAmount(userId, todayStart, todayEnd);
        Integer todayInboundCount = calculateInboundCount(userId, todayStart, todayEnd);
        Integer todayOutboundCount = calculateOutboundCount(userId, todayStart, todayEnd);
        Integer todayDebtCount = calculateDebtCount(userId, todayStart, todayEnd);
        
        // 计算本月数据
        BigDecimal monthInboundAmount = calculateInboundAmount(userId, monthStart, monthEnd);
        BigDecimal monthOutboundAmount = calculateOutboundAmount(userId, monthStart, monthEnd);
        BigDecimal monthProfit = calculateProfit(userId, monthStart, monthEnd);
        BigDecimal monthDebtAmount = calculateDebtAmount(userId, monthStart, monthEnd);
        Integer monthInboundCount = calculateInboundCount(userId, monthStart, monthEnd);
        Integer monthOutboundCount = calculateOutboundCount(userId, monthStart, monthEnd);
        Integer monthDebtCount = calculateDebtCount(userId, monthStart, monthEnd);
        
        // 计算全年数据
        BigDecimal yearInboundAmount = calculateInboundAmount(userId, yearStart, yearEnd);
        BigDecimal yearOutboundAmount = calculateOutboundAmount(userId, yearStart, yearEnd);
        BigDecimal yearProfit = calculateProfit(userId, yearStart, yearEnd);
        BigDecimal yearDebtAmount = calculateDebtAmount(userId, yearStart, yearEnd);
        Integer yearInboundCount = calculateInboundCount(userId, yearStart, yearEnd);
        Integer yearOutboundCount = calculateOutboundCount(userId, yearStart, yearEnd);
        Integer yearDebtCount = calculateDebtCount(userId, yearStart, yearEnd);
        
        // 计算总欠款
        BigDecimal totalUnpaidAmount = calculateTotalUnpaidAmount(userId);
        
        // 计算产品销售排行
        List<StatisticsResp.ProductSaleRank> productSaleRanks = calculateProductSaleRanks(userId);
        
        // 创建并返回响应对象
        StatisticsResp resp = new StatisticsResp();
        
        // 为所有字段设置默认值0，确保前端显示正常
        resp.setTodayInboundAmount(todayInboundAmount);
        resp.setTodayOutboundAmount(todayOutboundAmount);
        resp.setTodayProfit(todayProfit);
        resp.setTodayDebtAmount(todayDebtAmount);
        resp.setTodayInboundCount(todayInboundCount);
        resp.setTodayOutboundCount(todayOutboundCount);
        resp.setTodayDebtCount(todayDebtCount);
        
        resp.setMonthInboundAmount(monthInboundAmount);
        resp.setMonthOutboundAmount(monthOutboundAmount);
        resp.setMonthProfit(monthProfit);
        resp.setMonthDebtAmount(monthDebtAmount);
        resp.setMonthInboundCount(monthInboundCount);
        resp.setMonthOutboundCount(monthOutboundCount);
        resp.setMonthDebtCount(monthDebtCount);
        
        resp.setYearInboundAmount(yearInboundAmount);
        resp.setYearOutboundAmount(yearOutboundAmount);
        resp.setYearProfit(yearProfit);
        resp.setYearDebtAmount(yearDebtAmount);
        resp.setYearInboundCount(yearInboundCount);
        resp.setYearOutboundCount(yearOutboundCount);
        resp.setYearDebtCount(yearDebtCount);
        
        // 待收欠款总额和总欠款金额应该是相同的
        resp.setTotalUnpaidDebt(totalUnpaidAmount);
        resp.setTotalUnpaidAmount(totalUnpaidAmount);
        resp.setProductSaleRanks(productSaleRanks);
        
        return resp;
    }

    private BigDecimal calculateInboundAmount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InboundOrder::getUserId, userId);
        // 处理CreateTime为null的情况，将null视为符合条件
        wrapper.and(w -> w.ge(InboundOrder::getCreateTime, startTime)
                .or().isNull(InboundOrder::getCreateTime))
            .and(w -> w.le(InboundOrder::getCreateTime, endTime)
                .or().isNull(InboundOrder::getCreateTime));
        
        System.out.println("Calculating inbound amount for userId: " + userId + ", startTime: " + startTime + ", endTime: " + endTime);
        List<InboundOrder> orders = inboundOrderMapper.selectList(wrapper);
        System.out.println("Found " + orders.size() + " inbound orders");
        orders.forEach(order -> System.out.println("Inbound order: ID=" + order.getId() + ", UserId=" + order.getUserId() + ", CreateTime=" + order.getCreateTime() + ", TotalAmount=" + order.getTotalAmount()));
        
        return orders.stream()
            .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateOutboundAmount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundOrder::getUserId, userId);
        // 处理CreateTime为null的情况，将null视为符合条件
        wrapper.and(w -> w.ge(OutboundOrder::getCreateTime, startTime)
                .or().isNull(OutboundOrder::getCreateTime))
            .and(w -> w.le(OutboundOrder::getCreateTime, endTime)
                .or().isNull(OutboundOrder::getCreateTime));
        
        List<OutboundOrder> orders = outboundOrderMapper.selectList(wrapper);
        return orders.stream()
            .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDebtAmount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Debt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Debt::getUserId, userId);
        wrapper.eq(Debt::getStatus, 1);
        // 处理CreateTime为null的情况，将null视为符合条件
        wrapper.and(w -> w.ge(Debt::getCreateTime, startTime)
                .or().isNull(Debt::getCreateTime))
            .and(w -> w.le(Debt::getCreateTime, endTime)
                .or().isNull(Debt::getCreateTime));
        
        List<Debt> debts = debtMapper.selectList(wrapper);
        return debts.stream()
            .map(debt -> debt.getRemainingAmount() != null ? debt.getRemainingAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateProfit(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundOrder::getUserId, userId);
        // 处理CreateTime为null的情况，将null视为符合条件
        wrapper.and(w -> w.ge(OutboundOrder::getCreateTime, startTime)
                .or().isNull(OutboundOrder::getCreateTime))
            .and(w -> w.le(OutboundOrder::getCreateTime, endTime)
                .or().isNull(OutboundOrder::getCreateTime));
        
        List<OutboundOrder> orders = outboundOrderMapper.selectList(wrapper);
        return orders.stream()
            .map(order -> order.getProfit() != null ? order.getProfit() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalUnpaidAmount(Long userId) {
        LambdaQueryWrapper<Debt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Debt::getUserId, userId);
        // 查询未结清状态的债务，包括status为1和status为null的情况
        wrapper.and(w -> w.eq(Debt::getStatus, 1).or().isNull(Debt::getStatus));
        
        List<Debt> debts = debtMapper.selectList(wrapper);
        return debts.stream()
            .map(debt -> debt.getRemainingAmount() != null ? debt.getRemainingAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private Integer calculateInboundCount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InboundOrder::getUserId, userId);
        // 处理CreateTime为null的情况，将null视为符合条件
        wrapper.and(w -> w.ge(InboundOrder::getCreateTime, startTime)
                .or().isNull(InboundOrder::getCreateTime))
            .and(w -> w.le(InboundOrder::getCreateTime, endTime)
                .or().isNull(InboundOrder::getCreateTime));
        return inboundOrderMapper.selectCount(wrapper).intValue();
    }
    
    private Integer calculateOutboundCount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundOrder::getUserId, userId);
        // 处理CreateTime为null的情况，将null视为符合条件
        wrapper.and(w -> w.ge(OutboundOrder::getCreateTime, startTime)
                .or().isNull(OutboundOrder::getCreateTime))
            .and(w -> w.le(OutboundOrder::getCreateTime, endTime)
                .or().isNull(OutboundOrder::getCreateTime));
        return outboundOrderMapper.selectCount(wrapper).intValue();
    }
    
    private Integer calculateDebtCount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Debt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Debt::getUserId, userId);
        // 处理CreateTime为null的情况，将null视为符合条件
        wrapper.and(w -> w.ge(Debt::getCreateTime, startTime)
                .or().isNull(Debt::getCreateTime))
            .and(w -> w.le(Debt::getCreateTime, endTime)
                .or().isNull(Debt::getCreateTime));
        return debtMapper.selectCount(wrapper).intValue();
    }
    
    private List<StatisticsResp.ProductSaleRank> calculateProductSaleRanks(Long userId) {
        // 查询出库单，按产品分组统计销量和金额
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundOrder::getUserId, userId);
        
        List<OutboundOrder> orders = outboundOrderMapper.selectList(wrapper);
        
        // 按产品ID分组统计
        Map<Long, StatisticsResp.ProductSaleRank> productMap = new HashMap<>();
        for (OutboundOrder order : orders) {
            if (order.getProductId() == null) continue;
            
            StatisticsResp.ProductSaleRank rank = productMap.get(order.getProductId());
            if (rank == null) {
                rank = new StatisticsResp.ProductSaleRank();
                rank.setProductId(order.getProductId());
                rank.setTotalWeight(BigDecimal.ZERO);
                rank.setTotalAmount(BigDecimal.ZERO);
                productMap.put(order.getProductId(), rank);
            }
            
            // 累加销量和金额
            if (order.getWeight() != null) {
                rank.setTotalWeight(rank.getTotalWeight().add(order.getWeight()));
            }
            if (order.getTotalAmount() != null) {
                rank.setTotalAmount(rank.getTotalAmount().add(order.getTotalAmount()));
            }
        }
        
        // 填充产品详细信息
        for (StatisticsResp.ProductSaleRank rank : productMap.values()) {
            Product product = productMapper.selectById(rank.getProductId());
            if (product != null) {
                rank.setProductName(product.getName());
                rank.setVariety(product.getVariety());
                rank.setGrade(product.getGrade());
                rank.setUnit(product.getUnit());
            }
        }
        
        // 将Map转换为List
        List<StatisticsResp.ProductSaleRank> ranks = new ArrayList<>(productMap.values());
        
        // 按销售额降序排序
        ranks.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));
        
        return ranks;
    }
}
