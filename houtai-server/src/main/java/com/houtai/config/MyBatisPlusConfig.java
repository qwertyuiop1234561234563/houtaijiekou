//package com.houtai.config;
//
//
//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MyBatisPlusConfig {
//
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//
//        // 分页插件
//        PaginationInnerInterceptor paginationInterceptor =
//                new PaginationInnerInterceptor(DbType.MYSQL);
//        paginationInterceptor.setMaxLimit(1000L); // 单页分页条数限制
//        paginationInterceptor.setOverflow(true);  // 页码超出范围时回到第一页
//
//        interceptor.addInnerInterceptor(paginationInterceptor);
//        return interceptor;
//    }
//}
