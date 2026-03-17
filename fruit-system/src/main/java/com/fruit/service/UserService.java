package com.fruit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        
        // 数据隔离：管理员可以查看自己和自己创建的员工，员工只能查看自己
        if (currentUser.getRole() == 1) { // 老板（管理员）
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
        
        // 数据安全检查：管理员只能查看自己和自己创建的员工，员工只能查看自己
        if (currentUser.getRole() == 1) { // 老板（管理员）
            if (!user.getId().equals(currentUserId) && !user.getParentId().equals(currentUserId)) {
                throw new BusinessException("无权查看该用户信息");
            }
        } else { // 员工
            if (!user.getId().equals(currentUserId)) {
                throw new BusinessException("无权查看该用户信息");
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
        user.setParentId(currentUserId); // 设置上级用户ID为当前用户
        
        userMapper.insert(user);
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
        
        // 数据安全检查：管理员只能修改自己和自己创建的员工，员工只能修改自己
        if (currentUser.getRole() == 1) { // 老板（管理员）
            if (!user.getId().equals(currentUserId) && !user.getParentId().equals(currentUserId)) {
                throw new BusinessException("无权修改该用户信息");
            }
        } else { // 员工
            if (!user.getId().equals(currentUserId)) {
                throw new BusinessException("无权修改该用户信息");
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
        }
        
        // 只有管理员可以修改parentId，且只能设置为自己
        if (currentUser.getRole() == 1 && req.getParentId() != null) {
            user.setParentId(req.getParentId());
        }
        
        userMapper.updateById(user);
    }

    public void delete(Long id) {
        // 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        userMapper.deleteById(id);
    }
}
