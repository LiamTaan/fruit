package com.fruit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.common.exception.BusinessException;
import com.fruit.dto.req.ChangePasswordReq;
import com.fruit.dto.req.LoginReq;
import com.fruit.dto.req.UserCreateReq;
import com.fruit.dto.req.UserUpdateReq;
import com.fruit.dto.resp.LoginResp;
import com.fruit.dto.resp.UserInfoResp;
import com.fruit.entity.User;
import com.fruit.mapper.UserMapper;
import com.fruit.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public LoginResp login(LoginReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, req.getUsername()).or().eq(User::getPhone, req.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        UserInfoResp userInfo = new UserInfoResp(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getPhone(),
            user.getRole(),
            user.getStatus(),
            user.getParentId()
        );
        
        return new LoginResp(token, userInfo);
    }

    public UserInfoResp getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return new UserInfoResp(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getPhone(),
            user.getRole(),
            user.getStatus(),
            user.getParentId()
        );
    }

    public void changePassword(Long userId, ChangePasswordReq req) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        
        if (req.getNewPassword().length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }
        
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userMapper.updateById(user);
    }

    public IPage<UserInfoResp> page(int pageNum, int pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 获取当前用户ID和角色
        Long currentUserId = getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        // 数据隔离：超级管理员（admin）可以查看所有用户
        if ("admin".equals(currentUser.getUsername())) {
            // 超级管理员可以查看所有用户，不需要数据隔离
        } else if (currentUser.getRole() == 1) { // 普通管理员
            wrapper.and(w -> w.eq(User::getId, currentUserId) // 自己
                            .or().eq(User::getParentId, currentUserId)); // 自己创建的员工
        } else { // 员工
            wrapper.eq(User::getId, currentUserId); // 只能查看自己
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                            .or().like(User::getNickname, keyword)
                            .or().like(User::getPhone, keyword));
        }
        
        IPage<User> userPage = userMapper.selectPage(page, wrapper);
        
        return userPage.convert(user -> new UserInfoResp(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getPhone(),
            user.getRole(),
            user.getStatus(),
            user.getParentId()
        ));
    }

    public UserInfoResp getById(Long id) {
        // 获取当前用户ID和角色
        Long currentUserId = getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        // 获取目标用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 数据安全检查：超级管理员（admin）可以查看所有用户
        if (!"admin".equals(currentUser.getUsername())) {
            // 普通管理员只能查看自己和自己创建的员工，员工只能查看自己
            if (currentUser.getRole() == 1) { // 普通管理员
                if (!user.getId().equals(currentUserId) && !user.getParentId().equals(currentUserId)) {
                    throw new BusinessException("无权查看该用户信息");
                }
            } else { // 员工
                if (!user.getId().equals(currentUserId)) {
                    throw new BusinessException("无权查看该用户信息");
                }
            }
        }
        
        return new UserInfoResp(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getPhone(),
            user.getRole(),
            user.getStatus(),
            user.getParentId()
        );
    }

    public void create(UserCreateReq req) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, req.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        wrapper.clear();
        wrapper.eq(User::getPhone, req.getPhone());
        if (userMapper.selectOne(wrapper) != null) {
            throw new BusinessException("手机号已存在");
        }
        
        // 获取当前用户ID作为parentId
        Long currentUserId = getCurrentUserId();
        
        // 创建用户
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole() != null ? req.getRole() : 2); // 默认员工角色
        user.setStatus(req.getStatus() != null ? req.getStatus() : 1); // 默认启用
        
        // 设置上级用户ID
        Integer role = req.getRole() != null ? req.getRole() : 2;
        if (role == 1) {
            // 管理员角色不需要设置上级
            user.setParentId(null);
        } else {
            // 员工角色，使用前端传入的parentId，如果没有传入则使用当前用户ID
            user.setParentId(req.getParentId() != null ? req.getParentId() : currentUserId);
        }
        
        userMapper.insert(user);
    }
    
    /**
     * 获取所有管理员列表
     * 用于在创建/编辑用户时选择所属管理员
     */
    public List<UserInfoResp> listAdmins() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, 1); // 角色为管理员
        
        // 获取当前用户ID
        Long currentUserId = getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        // 数据隔离：超级管理员可以查看所有管理员，普通管理员只能查看自己
        if (!"admin".equals(currentUser.getUsername())) {
            wrapper.eq(User::getId, currentUserId);
        }
        
        wrapper.orderByAsc(User::getCreateTime);
        
        List<User> admins = userMapper.selectList(wrapper);
        
        return admins.stream().map(admin -> new UserInfoResp(
            admin.getId(),
            admin.getUsername(),
            admin.getNickname(),
            admin.getPhone(),
            admin.getRole(),
            admin.getStatus(),
            admin.getParentId()
        )).collect(java.util.stream.Collectors.toList());
    }

    public void update(UserUpdateReq req) {
        // 获取当前用户ID和角色
        Long currentUserId = getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        // 检查用户是否存在
        User user = userMapper.selectById(req.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 数据安全检查：超级管理员可以修改所有用户，普通管理员只能修改自己和自己创建的员工，员工只能修改自己
        if (!"admin".equals(currentUser.getUsername())) {
            if (currentUser.getRole() == 1) { // 普通管理员
                if (!user.getId().equals(currentUserId) && !user.getParentId().equals(currentUserId)) {
                    throw new BusinessException("无权修改该用户信息");
                }
            } else { // 员工
                if (!user.getId().equals(currentUserId)) {
                    throw new BusinessException("无权修改该用户信息");
                }
            }
        }
        
        // 检查用户名是否已被其他用户使用
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, req.getUsername())
               .ne(User::getId, req.getId());
        if (userMapper.selectOne(wrapper) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查手机号是否已被其他用户使用
        wrapper.clear();
        wrapper.eq(User::getPhone, req.getPhone())
               .ne(User::getId, req.getId());
        if (userMapper.selectOne(wrapper) != null) {
            throw new BusinessException("手机号已存在");
        }
        
        // 更新用户信息
        user.setUsername(req.getUsername());
        user.setNickname(req.getNickname());
        user.setPhone(req.getPhone());
        
        // 只有管理员可以修改角色和状态
        if (currentUser.getRole() == 1) {
            user.setRole(req.getRole());
            user.setStatus(req.getStatus());
            
            // 创建UpdateWrapper
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", user.getId());
            updateWrapper.set("role", req.getRole());
            updateWrapper.set("status", req.getStatus());
            
            // 如果角色改为管理员，清除所属管理员
            if (req.getRole() == 1) {
                updateWrapper.set("parent_id", null);
            } else if (req.getParentId() != null) {
                // 只有员工角色可以设置所属管理员
                updateWrapper.set("parent_id", req.getParentId());
            }
            
            // 更新用户信息
            userMapper.update(user, updateWrapper);
        } else {
            // 如果不是管理员修改，只更新基本信息
            userMapper.updateById(user);
        }
    }

    public void delete(Long id) {
        // 获取当前用户ID和角色
        Long currentUserId = getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        // 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 数据安全检查：超级管理员可以删除所有用户，普通管理员只能删除自己创建的员工
        if (!"admin".equals(currentUser.getUsername())) {
            if (currentUser.getRole() == 1) { // 普通管理员
                if (!user.getParentId().equals(currentUserId)) {
                    throw new BusinessException("无权删除该用户信息");
                }
            } else { // 员工
                throw new BusinessException("员工无权删除用户");
            }
        }
        
        userMapper.deleteById(id);
    }
}
