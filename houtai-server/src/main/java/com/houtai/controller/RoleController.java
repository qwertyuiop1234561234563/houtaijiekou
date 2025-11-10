package com.houtai.controller;

import com.houtai.entity.PageResult;
import com.houtai.entity.Role;
import com.houtai.entity.RolePageParams;
import com.houtai.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表（分页）
     */
    @GetMapping("/list")
    public PageResult<Role> getRoleList(RolePageParams params) {
        return roleService.getRoleList(params);
    }

    /**
     * 添加角色
     */
    @PostMapping("/add")
    public Long addRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    /**
     * 更新角色
     */
    @PostMapping("/update")
    public boolean updateRole(@RequestBody Role role) {
        if (role.getId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        return roleService.updateRole(role.getId(), role);
    }

    /**
     * 删除角色
     */
    @PostMapping("/delete")
    public boolean deleteRole(@RequestBody Role role) {
        if (role.getId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        return roleService.deleteRole(role.getId());
    }

    /**
     * 批量删除角色
     */
    @PostMapping("/batchDelete")
    public boolean batchDeleteRoles(@RequestBody List<Long> ids) {
        return roleService.batchDeleteRoles(ids);
    }

    /**
     * 获取所有权限列表
     */
    @GetMapping("/permissions")
    public List<String> getAllPermissions() {
        return roleService.getAllPermissions();
    }

    /**
     * 更新角色权限
     */
    @PostMapping("/updatePermissions")
    public boolean updateRolePermissions(@RequestParam Long roleId,
                                         @RequestBody List<String> permissions) {
        return roleService.updateRolePermissions(roleId, permissions);
    }
}