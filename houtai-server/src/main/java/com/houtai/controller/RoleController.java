package com.houtai.controller;

import com.houtai.entity.ApiResponse;
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
@RequestMapping("/system")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/role/list")
    public ApiResponse<PageResult<Role>> getRoleList(RolePageParams params) {
        log.info("接收到角色列表查询请求: page={}, pageSize={}, name={}, code={}",
                params.getPage(), params.getPageSize(), params.getName(), params.getCode());
        try {
            PageResult<Role> result = roleService.getRoleList(params);
            log.info("角色列表查询成功: 共 {} 条记录", result.getList().size());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("角色列表查询失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有权限列表 - 修正路径为 /system/permissions
     */

    @GetMapping("/permissions")
    public ApiResponse<List<String>> getAllPermissions() {
        log.info("获取所有权限列表");
        try {
            List<String> permissions = roleService.getAllPermissions();
            log.info("返回权限列表: {} 个权限", permissions.size());
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            log.error("获取权限列表失败", e);
            return ApiResponse.error("获取权限列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加角色
     */
    @PostMapping("/role/add")
    public Long addRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    /**
     * 更新角色
     */
    @PostMapping("/role/update")
    public boolean updateRole(@RequestBody Role role) {
        if (role.getId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        return roleService.updateRole(role.getId().longValue(), role);
    }

    /**
     * 删除角色
     */
    @PostMapping("/role/delete")
    public boolean deleteRole(@RequestBody Role role) {
        if (role.getId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        return roleService.deleteRole(role.getId().longValue());
    }


    /**
     * 批量删除角色
     */
    @PostMapping("/role/batchDelete")
    public boolean batchDeleteRoles(@RequestBody List<Long> ids) {
        return roleService.batchDeleteRoles(ids);
    }

    /**
     * 更新角色权限
     */
    @PostMapping("/role/updatePermissions")
    public boolean updateRolePermissions(@RequestParam Long roleId,
                                         @RequestBody List<String> permissions) {
        return roleService.updateRolePermissions(roleId, permissions);
    }

    /**
     * 测试接口 - 检查数据库数据
     */
    @GetMapping("/role/test")
    public List<Role> testRoles() {
        log.info("测试角色数据查询");
        List<Role> roles = roleService.list();
        log.info("直接查询到 {} 条角色记录", roles.size());
        roles.forEach(role -> log.info("角色: id={}, name={}, code={}, permission={}",
                role.getId(), role.getName(), role.getCode(), role.getPermissionStr()));
        return roles;
    }
}