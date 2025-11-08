package com.houtai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houtai.entity.User;
import com.houtai.mapper.UserMapper;
import com.houtai.dto.LoginDTO;
import com.houtai.entity.PageParams;
import com.houtai.service.UserService;
import com.houtai.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_EXPIRE = 7; // 7天

    @Override
    public String login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getPassword, loginDTO.getPassword());
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new RuntimeException("账号或者密码不正确");
        }

        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 转换角色字符串为列表
        if (user.getRoles() != null) {
            user.setRoleList(Arrays.asList(user.getRoles().split(",")));
        }
        user.setToken(token);

        // 存储到Redis
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, user,
                TOKEN_EXPIRE, TimeUnit.DAYS);

        return token;
    }

    @Override
    public User getUserInfo(String token) {
        User user = (User) redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (user == null) {
            throw new RuntimeException("获取用户信息失败");
        }

        // 转换角色字符串为列表
        if (user.getRoles() != null) {
            user.setRoleList(Arrays.asList(user.getRoles().split(",")));
        }

        return user;
    }

    @Override
    public User getCurrentUserInfo() {
        String token = UserContext.getToken();
        if (token == null) {
            throw new RuntimeException("未找到token信息");
        }
        return getUserInfo(token);
    }

    @Override
    public Page<User> getUserList(PageParams params) {
        Page<User> page = new Page<>(params.getPage(), params.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (params.getUsername() != null && !params.getUsername().isEmpty()) {
            wrapper.like(User::getUsername, params.getUsername());
        }

        return this.page(page, wrapper);
    }

    @Override
    public Long addUser(User user) {
        // 将角色列表转换为字符串
        if (user.getRoleList() != null && !user.getRoleList().isEmpty()) {
            user.setRoles(String.join(",", user.getRoleList()));
        }
        this.save(user);
        return user.getId();
    }

    @Override
    public boolean updateUser(Long id, User user) {
        user.setId(id);
        // 将角色列表转换为字符串
        if (user.getRoleList() != null && !user.getRoleList().isEmpty()) {
            user.setRoles(String.join(",", user.getRoleList()));
        }
        return this.updateById(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return this.removeById(id);
    }
}