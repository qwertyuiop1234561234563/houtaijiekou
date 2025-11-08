package com.houtai.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDate;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String name;
    private String password;
    private String avatar;
    private String roles; // 数据库中是varchar，存储角色字符串

    @TableField("create_time")
    private LocalTime createTime;

    @TableField("update_time")
    private LocalTime updateTime;

    // 用于业务逻辑的角色列表
    @TableField(exist = false)
    private java.util.List<String> roleList;

    // token字段（不存储到数据库）
    @TableField(exist = false)
    private String token;
}