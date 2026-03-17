package com.fruit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.exception.BusinessException;
import com.fruit.dto.req.RepaymentReq;
import com.fruit.dto.resp.DebtResp;
import com.fruit.entity.Customer;
import com.fruit.entity.Debt;
import com.fruit.entity.OutboundOrder;
import com.fruit.mapper.CustomerMapper;
import com.fruit.mapper.DebtMapper;
import com.fruit.mapper.OutboundOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtMapper debtMapper;
    private final CustomerMapper customerMapper;
    private final OutboundOrderMapper outboundOrderMapper;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<DebtResp> page(int pageNum, int pageSize, Integer status, String keyword) {
        Long userId = getCurrentUserId();
        Page<Debt> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Debt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Debt::getUserId, userId);
        if (status != null) {
            wrapper.eq(Debt::getStatus, status);
        }
        
        // 添加搜索逻辑
        if (keyword != null && !keyword.isEmpty()) {
            // 搜索客户名称
            LambdaQueryWrapper<Customer> customerWrapper = new LambdaQueryWrapper<>();
            customerWrapper.like(Customer::getName, keyword);
            List<Long> customerIds = customerMapper.selectList(customerWrapper)
                .stream()
                .map(Customer::getId)
                .collect(Collectors.toList());
            
            // 搜索出库单号
            LambdaQueryWrapper<OutboundOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.like(OutboundOrder::getOrderNo, keyword);
            List<Long> orderIds = outboundOrderMapper.selectList(orderWrapper)
                .stream()
                .map(OutboundOrder::getId)
                .collect(Collectors.toList());
            
            // 组合搜索条件
            if (!customerIds.isEmpty() || !orderIds.isEmpty()) {
                wrapper.and(w -> {
                    if (!customerIds.isEmpty()) {
                        w.in(Debt::getCustomerId, customerIds);
                    }
                    if (!orderIds.isEmpty()) {
                        if (!customerIds.isEmpty()) {
                            w.or();
                        }
                        w.in(Debt::getOutboundOrderId, orderIds);
                    }
                });
            } else {
                // 如果没有匹配结果，返回空列表
                wrapper.eq(Debt::getId, -1);
            }
        }
        
        wrapper.orderByDesc(Debt::getCreateTime);
        
        Page<Debt> resultPage = debtMapper.selectPage(page, wrapper);
        
        Page<DebtResp> respPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<DebtResp> respList = resultPage.getRecords().stream()
            .map(this::convertToResp)
            .collect(Collectors.toList());
        respPage.setRecords(respList);
        
        return respPage;
    }

    public DebtResp getById(Long id) {
        Debt debt = debtMapper.selectById(id);
        if (debt == null) {
            throw new BusinessException("欠款记录不存在");
        }
        return convertToResp(debt);
    }

    @Transactional
    public void repayment(RepaymentReq req) {
        Debt debt = debtMapper.selectById(req.getDebtId());
        if (debt == null) {
            throw new BusinessException("欠款记录不存在");
        }
        if (debt.getStatus() == 2) {
            throw new BusinessException("该欠款已结清");
        }
        
        if (req.getAmount().compareTo(debt.getRemainingAmount()) > 0) {
            throw new BusinessException("还款金额不能超过剩余欠款");
        }
        
        debt.setPaidAmount(debt.getPaidAmount().add(req.getAmount()));
        debt.setRemainingAmount(debt.getRemainingAmount().subtract(req.getAmount()));
        
        if (debt.getRemainingAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            debt.setStatus(2);
        }
        
        debtMapper.updateById(debt);
    }

    private DebtResp convertToResp(Debt debt) {
        DebtResp resp = new DebtResp();
        BeanUtils.copyProperties(debt, resp);
        resp.setStatusName(getStatusName(debt.getStatus()));
        
        Customer customer = customerMapper.selectById(debt.getCustomerId());
        if (customer != null) {
            resp.setCustomerName(customer.getName());
        }
        
        OutboundOrder order = outboundOrderMapper.selectById(debt.getOutboundOrderId());
        if (order != null) {
            resp.setOutboundOrderNo(order.getOrderNo());
        }
        
        return resp;
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "未结清";
            case 2:
                return "已结清";
            default:
                return "未知";
        }
    }
}
