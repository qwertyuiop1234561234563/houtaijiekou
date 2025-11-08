package com.houtai.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.houtai.utils.ThreadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginInterCeptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOKEN_PREFIX = "token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }

        System.out.println("拦截器执行，路径: " + request.getRequestURI());

        String token = request.getHeader("token");
        System.out.println("拦截器获取到token: " + token);

        if (token == null || token.trim().isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("未找到token信息");
            return false;
        }

        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String userInfoJson = operations.get(TOKEN_PREFIX + token);
            System.out.println("Redis中查询到的用户信息: " + userInfoJson);

            if (userInfoJson == null) {
                throw new RuntimeException("token无效或已过期");
            }

            // 解析用户信息
            Map<String, Object> userInfo = objectMapper.readValue(userInfoJson,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

            // ✅ 修复：统一处理 ID 为字符串
            Object idObj = userInfo.get("id");
            if (idObj != null) {
                // 确保 ID 存储为字符串，避免类型问题
                userInfo.put("id", idObj.toString());
            }

            System.out.println("处理后的用户信息: " + userInfo);

            // 把用户信息存储到ThreadLocal中
            ThreadLocalUtils.set(userInfo);
            System.out.println("ThreadLocal设置成功");

            return true;
        } catch (Exception e) {
            System.out.println("token验证失败: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(401);
            response.getWriter().write("token验证失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtils.remove();
        System.out.println("ThreadLocal清理完成");
    }
}