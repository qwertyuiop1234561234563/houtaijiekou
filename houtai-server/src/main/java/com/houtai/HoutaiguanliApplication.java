package com.houtai;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@EnableCaching//开启缓存注解功能
@EnableScheduling//开启定时任务注解功能
@Slf4j
public class HoutaiguanliApplication {
    public static void main(String[] args) {
        SpringApplication.run(HoutaiguanliApplication.class, args);
        log.info("server started");
    }
}
