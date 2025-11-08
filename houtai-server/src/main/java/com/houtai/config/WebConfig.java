package com.houtai.config;

import com.houtai.interceptor.LoginInterCeptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//注册拦截器
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterCeptor loginInterCeptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //放行登录和注册
        registry.addInterceptor(loginInterCeptor).excludePathPatterns("/users/login","/users/register");
    }
}
