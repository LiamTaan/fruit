package com.fruit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.exception.BusinessException;
import com.fruit.dto.req.ProductReq;
import com.fruit.dto.resp.ProductResp;
import com.fruit.entity.Inventory;
import com.fruit.entity.Product;
import com.fruit.entity.User;
import com.fruit.mapper.InventoryMapper;
import com.fruit.mapper.ProductMapper;
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
public class ProductService {

    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final UserMapper userMapper;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<ProductResp> page(int pageNum, int pageSize, String keyword) {
        Long userId = getCurrentUserId();
        Page<Product> page = new Page<>(pageNum, pageSize);
        
        // 获取当前用户信息
        User currentUser = userMapper.selectById(userId);
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        // 实现产品目录的共享：
        // - 员工可以看到自己和所属管理员创建的产品
        // - 管理员可以看到自己和下属员工创建的产品
        if (currentUser.getRole() == 2) { // 员工
            wrapper.and(w -> w.eq(Product::getUserId, userId) // 自己创建的
                            .or().eq(Product::getUserId, currentUser.getParentId())); // 所属管理员创建的
        } else { // 管理员
            // 管理员可以看到自己和下属员工的产品
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.and(w -> w.eq(User::getId, userId) // 管理员自己
                               .or().eq(User::getParentId, userId)); // 下属员工
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            wrapper.in(Product::getUserId, userIds);
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                .or().like(Product::getVariety, keyword));
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        
        Page<Product> resultPage = productMapper.selectPage(page, wrapper);
        
        Page<ProductResp> respPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<ProductResp> respList = resultPage.getRecords().stream()
            .map(this::convertToResp)
            .collect(Collectors.toList());
        respPage.setRecords(respList);
        
        return respPage;
    }

    public List<ProductResp> listAll() {
        Long userId = getCurrentUserId();
        
        // 获取当前用户信息
        User currentUser = userMapper.selectById(userId);
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        // 实现产品目录的共享：
        // - 员工可以看到自己和所属管理员创建的产品
        // - 管理员可以看到自己和下属员工创建的产品
        if (currentUser.getRole() == 2) { // 员工
            wrapper.and(w -> w.eq(Product::getUserId, userId) // 自己创建的
                            .or().eq(Product::getUserId, currentUser.getParentId())); // 所属管理员创建的
        } else { // 管理员
            // 管理员可以看到自己和下属员工的产品
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.and(w -> w.eq(User::getId, userId) // 管理员自己
                               .or().eq(User::getParentId, userId)); // 下属员工
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            wrapper.in(Product::getUserId, userIds);
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectList(wrapper).stream()
            .map(this::convertToResp)
            .collect(Collectors.toList());
    }

    public ProductResp getById(Long id) {
        Long userId = getCurrentUserId();
        
        // 获取当前用户信息
        User currentUser = userMapper.selectById(userId);
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("果品不存在");
        }
        
        // 数据安全检查：
        // - 员工只能访问自己和所属管理员创建的产品
        // - 管理员可以访问自己和下属员工创建的产品
        if (currentUser.getRole() == 2) { // 员工
            if (!product.getUserId().equals(userId) && !product.getUserId().equals(currentUser.getParentId())) {
                throw new BusinessException("无权访问该果品");
            }
        } else { // 管理员
            // 查询管理员自己和下属员工的ID列表
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.and(w -> w.eq(User::getId, userId) // 管理员自己
                               .or().eq(User::getParentId, userId)); // 下属员工
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            
            if (!userIds.contains(product.getUserId())) {
                throw new BusinessException("无权访问该果品");
            }
        }
        
        return convertToResp(product);
    }

    public void create(ProductReq req) {
        Long userId = getCurrentUserId();
        Product product = new Product();
        BeanUtils.copyProperties(req, product);
        product.setUserId(userId);
        productMapper.insert(product);
    }

    public void update(ProductReq req) {
        Product product = productMapper.selectById(req.getId());
        if (product == null) {
            throw new BusinessException("果品不存在");
        }
        BeanUtils.copyProperties(req, product);
        productMapper.updateById(product);
    }

    public void delete(Long id) {
        productMapper.deleteById(id);
    }
    
    public void disable(Long id, Boolean disabled) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("果品不存在");
        }
        product.setDisabled(disabled);
        productMapper.updateById(product);
    }

    private ProductResp convertToResp(Product product) {
        ProductResp resp = new ProductResp();
        BeanUtils.copyProperties(product, resp);
        
        // 查询库存信息
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getProductId, product.getId());
        wrapper.eq(Inventory::getUserId, product.getUserId());
        Inventory inventory = inventoryMapper.selectOne(wrapper);
        
        // 设置库存数量，默认0
        BigDecimal stock = BigDecimal.ZERO;
        if (inventory != null && inventory.getQuantity() != null) {
            stock = inventory.getQuantity();
        }
        resp.setStock(stock);
        
        return resp;
    }
}
