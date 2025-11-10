package com.houtai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 权限列表（非数据库字段）
    @TableField(exist = false)
    private List<String> permissions;

    // 权限字符串（数据库字段，存储为逗号分隔）
    private String permissionsStr;
}