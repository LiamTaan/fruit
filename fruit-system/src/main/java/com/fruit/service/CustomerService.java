package com.fruit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.exception.BusinessException;
import com.fruit.dto.req.CustomerReq;
import com.fruit.dto.resp.CustomerResp;
import com.fruit.entity.Customer;
import com.fruit.entity.Debt;
import com.fruit.entity.OutboundOrder;
import com.fruit.entity.User;
import com.fruit.mapper.CustomerMapper;
import com.fruit.mapper.DebtMapper;
import com.fruit.mapper.OutboundOrderMapper;
import com.fruit.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final DebtMapper debtMapper;
    private final OutboundOrderMapper outboundOrderMapper;
    private final UserMapper userMapper;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<CustomerResp> page(int pageNum, int pageSize, String keyword, Integer category) {
        Long userId = getCurrentUserId();
        Page<Customer> page = new Page<>(pageNum, pageSize);
        
        // 获取当前用户信息
        User currentUser = userMapper.selectById(userId);
        
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        
        // 实现业务数据的隔离：
        // - 管理员可以看到自己和下属员工创建的客户
        // - 员工只能看到自己创建的客户
        if (currentUser.getRole() == 1) { // 管理员
            // 查询管理员自己和下属员工的ID列表
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.and(w -> w.eq(User::getId, userId) // 管理员自己
                               .or().eq(User::getParentId, userId)); // 下属员工
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            
            wrapper.in(Customer::getUserId, userIds);
        } else { // 员工
            wrapper.eq(Customer::getUserId, userId); // 只能看到自己创建的客户
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Customer::getName, keyword)
                .or().like(Customer::getPhone, keyword));
        }
        if (category != null && category != 3) {
            // 只筛选优质客户和一般客户，欠款客户需要在转换后处理
            wrapper.eq(Customer::getCategory, category);
        }
        wrapper.orderByDesc(Customer::getCreateTime);
        
        Page<Customer> resultPage = customerMapper.selectPage(page, wrapper);
        
        Page<CustomerResp> respPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<CustomerResp> respList = resultPage.getRecords().stream()
            .map(this::convertToResp)
            .filter(resp -> {
                // 筛选欠款客户：当category为3时，只返回欠款金额大于0的客户
                if (category == null || category != 3) {
                    return true;
                }
                return resp.getDebtAmount() != null && resp.getDebtAmount().compareTo(BigDecimal.ZERO) > 0;
            })
            .collect(Collectors.toList());
        respPage.setRecords(respList);
        
        return respPage;
    }

    public CustomerResp getById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        
        // 数据安全检查
        Long userId = getCurrentUserId();
        User currentUser = userMapper.selectById(userId);
        
        if (currentUser.getRole() == 1) { // 管理员
            // 查询管理员自己和下属员工的ID列表
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.and(w -> w.eq(User::getId, userId) // 管理员自己
                               .or().eq(User::getParentId, userId)); // 下属员工
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            
            if (!userIds.contains(customer.getUserId())) {
                throw new BusinessException("无权查看该客户");
            }
        } else { // 员工
            if (!customer.getUserId().equals(userId)) {
                throw new BusinessException("无权查看该客户");
            }
        }
        
        return convertToResp(customer);
    }

    public void create(CustomerReq req) {
        Long userId = getCurrentUserId();
        Customer customer = new Customer();
        BeanUtils.copyProperties(req, customer);
        customer.setUserId(userId);
        customerMapper.insert(customer);
    }

    public void update(CustomerReq req) {
        Customer customer = customerMapper.selectById(req.getId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        BeanUtils.copyProperties(req, customer);
        customerMapper.updateById(customer);
    }

    public void delete(Long id) {
        customerMapper.deleteById(id);
    }

    private CustomerResp convertToResp(Customer customer) {
        CustomerResp resp = new CustomerResp();
        BeanUtils.copyProperties(customer, resp);
        resp.setCategoryName(getCategoryName(customer.getCategory()));
        
        // 前端使用createdAt，后端使用createTime，需要赋值
        resp.setCreatedAt(customer.getCreateTime());
        
        // 计算客户总交易额
        LambdaQueryWrapper<OutboundOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OutboundOrder::getCustomerId, customer.getId());
        orderWrapper.eq(OutboundOrder::getUserId, customer.getUserId());
        
        List<OutboundOrder> orders = outboundOrderMapper.selectList(orderWrapper);
        BigDecimal totalAmount = orders.stream()
            .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算客户总欠款金额
        LambdaQueryWrapper<Debt> debtWrapper = new LambdaQueryWrapper<>();
        debtWrapper.eq(Debt::getCustomerId, customer.getId());
        debtWrapper.eq(Debt::getUserId, customer.getUserId());
        debtWrapper.eq(Debt::getStatus, 1); // 只计算未结清的欠款
        
        List<Debt> debts = debtMapper.selectList(debtWrapper);
        BigDecimal totalDebtAmount = debts.stream()
            .map(debt -> debt.getRemainingAmount() != null ? debt.getRemainingAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        resp.setTotalAmount(totalAmount);
        resp.setDebtAmount(totalDebtAmount);
        resp.setTotalDebtAmount(totalDebtAmount);
        return resp;
    }

    private String getCategoryName(Integer category) {
        if (category == null) {
            return "未知";
        }
        switch (category) {
            case 1:
                return "优质客户";
            case 2:
                return "一般客户";
            case 3:
                return "欠款客户";
            default:
                return "未知";
        }
    }
}
