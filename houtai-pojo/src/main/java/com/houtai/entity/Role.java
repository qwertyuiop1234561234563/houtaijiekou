package com.houtai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String description;

    @TableField("create_time")
    private LocalDate createTime;  // 你的数据库是 date 类型

    @TableField("update_time")
    private LocalDate updateTime;  // 你的数据库是 date 类型

    // 注意：你的数据库字段是 permission（单数），不是 permissions_str
    @TableField("permission")
    private String permissionStr;

    // 权限列表（非数据库字段）
    @TableField(exist = false)
    private List<String> permissions;
}