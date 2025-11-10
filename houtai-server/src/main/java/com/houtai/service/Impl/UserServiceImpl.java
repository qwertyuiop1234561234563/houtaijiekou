package com.houtai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houtai.entity.PageResult;
import com.houtai.entity.User;
import com.houtai.mapper.UserMapper;
import com.houtai.dto.LoginDTO;
import com.houtai.entity.PageParams;
import com.houtai.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houtai.utils.ThreadLocalUtils;
import com.houtai.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    private static final long TOKEN_EXPIRE = 7; // 7天
    private static final String TOKEN_PREFIX = "token:";

    @Override
    public String login(LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getUsername());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getPassword, loginDTO.getPassword());
        User user = this.getOne(wrapper);

        if (user == null) {
            log.warn("登录失败: 用户名或密码错误 - {}", loginDTO.getUsername());
            throw new RuntimeException("账号或者密码不正确");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        log.info("生成UUID token: {} for user: {}", token, user.getUsername());

        try {
            // ✅ 修复：明确使用字符串存储 ID，避免类型问题
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId().toString()); // ✅ 存储为字符串
            userInfo.put("username", user.getUsername());
            userInfo.put("name", user.getName());
            userInfo.put("roles", user.getRoles());
            userInfo.put("avatar", user.getAvatar());

            String userInfoJson = objectMapper.writeValueAsString(userInfo);

            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(TOKEN_PREFIX + token, userInfoJson, TOKEN_EXPIRE, TimeUnit.DAYS);

            log.info("用户登录成功: {}, token: {}", user.getUsername(), token);
            return token;

        } catch (Exception e) {
            log.error("登录处理失败", e);
            throw new RuntimeException("登录处理失败");
        }
    }

    @Override
    public User getUserInfo(String token) {
            log.info("根据token获取用户信息: {}", token);

            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException("token不能为空");
            }

            try {
                ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
                String userInfoJson = operations.get(TOKEN_PREFIX + token);

                if (userInfoJson == null) {
                    log.warn("token无效或已过期: {}", token);
                    throw new RuntimeException("token无效或已过期");
                }

                // 解析用户信息
                Map<String, Object> userInfo = objectMapper.readValue(userInfoJson,
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

                // 处理 ID
                Long userId = null;
                Object idObj = userInfo.get("id");

                System.out.println("getUserInfo - ID对象类型: " + (idObj != null ? idObj.getClass().getName() : "null"));
                System.out.println("getUserInfo - ID对象值: " + idObj);

                if (idObj instanceof String) {
                    userId = Long.parseLong((String) idObj);
                } else if (idObj instanceof Long) {
                    userId = (Long) idObj;
                } else if (idObj instanceof Integer) {
                    userId = ((Integer) idObj).longValue();
                } else if (idObj != null) {
                    userId = Long.parseLong(idObj.toString());
                }

                if (userId == null) {
                    throw new RuntimeException("用户信息不完整，缺少ID");
                }

                // 从数据库查询用户信息
                User user = this.getById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在");
                }

                log.info("成功获取用户信息: {}", user.getUsername());
                return user;

            } catch (NumberFormatException e) {
                log.error("ID格式错误", e);
                throw new RuntimeException("用户ID格式错误");
            } catch (Exception e) {
                log.error("根据token获取用户信息失败", e);
                throw new RuntimeException("获取用户信息失败: " + e.getMessage());
            }
        }


    @Override
    public User getCurrentUserInfo() {
        try {
            // 从ThreadLocal获取用户信息
            Map<String, Object> userInfo = ThreadLocalUtils.get();
            System.out.println("从ThreadLocal获取用户信息: " + userInfo);

            if (userInfo == null || userInfo.isEmpty()) {
                throw new RuntimeException("未找到用户信息，请先登录");
            }

            // ✅ 修复：统一从字符串解析 ID
            Long userId = null;
            Object idObj = userInfo.get("id");

            System.out.println("ID对象类型: " + (idObj != null ? idObj.getClass().getName() : "null"));
            System.out.println("ID对象值: " + idObj);

            if (idObj instanceof String) {
                userId = Long.parseLong((String) idObj);
            } else if (idObj instanceof Long) {
                userId = (Long) idObj;
            } else if (idObj instanceof Integer) {
                userId = ((Integer) idObj).longValue();
            } else if (idObj != null) {
                userId = Long.parseLong(idObj.toString());
            }

            System.out.println("解析后的用户ID: " + userId);

            if (userId == null) {
                throw new RuntimeException("用户信息不完整，缺少ID");
            }

            // 从数据库查询用户信息
            User user = this.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            return user;
        } catch (NumberFormatException e) {
            log.error("ID格式错误", e);
            throw new RuntimeException("用户ID格式错误");
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<UserVO> getUserList(PageParams params) {
        // 使用 PageHelper 开始分页
        com.github.pagehelper.Page<UserVO> page = com.github.pagehelper.PageHelper.startPage(
                params.getPage(), params.getPageSize());

        // 执行关联查询
        List<UserVO> userList = userMapper.selectUserListWithRoles(params.getUsername());

        // 处理角色字段
        userList.forEach(user -> {
            // 处理角色ID列表
            if (user.getRoles() != null && !user.getRoles().trim().isEmpty()) {
                user.setRoleList(Arrays.asList(user.getRoles().split(",")));
            }

            // 处理角色名称列表
            if (user.getRoleNamesStr() != null && !user.getRoleNamesStr().trim().isEmpty()) {
                user.setRoleNames(Arrays.asList(user.getRoleNamesStr().split(",")));
            }
        });

        // 构建返回结果
        PageResult<UserVO> result = new PageResult<>();
        result.setList(userList);
        result.setTotal(page.getTotal());
        result.setPage(params.getPage());
        result.setPageSize(params.getPageSize());
        result.setPages(page.getPages());

        return result;
    }

    @Override
    public Long addUser(User user) {
        if (user.getRoleList() != null && !user.getRoleList().isEmpty()) {
            user.setRoles(String.join(",", user.getRoleList()));
        }
        this.save(user);
        return user.getId();
    }

    @Override
    public boolean updateUser(Long id, User user) {
        user.setId(id);
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