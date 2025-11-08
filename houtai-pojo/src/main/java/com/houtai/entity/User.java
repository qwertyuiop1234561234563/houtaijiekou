package com.houtai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String name;
    private String password;
    private String avatar;
    private String roles;

    // ✅ 修复：使用 LocalDateTime 或者添加 Jackson 注解
    @TableField("create_time")
    @JsonFormat(pattern = "HH:mm:ss") // 如果数据库存储的是时间
    private LocalTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime updateTime;

    // 或者改为 LocalDateTime（推荐）
    // @TableField("create_time")
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // private LocalDateTime createTime;

    // @TableField("update_time")
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<String> roleList;

    @TableField(exist = false)
    private String token;
}