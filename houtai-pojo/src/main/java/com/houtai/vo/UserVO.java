package com.houtai.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String name;
    private String roles; // 角色ID字符串
    private String avatar;
    // 其他用户字段...

    private List<String> roleList; // 角色ID列表
    private List<String> roleNames; // 角色名称列表
    private String roleNamesStr; // 角色名称字符串
}
