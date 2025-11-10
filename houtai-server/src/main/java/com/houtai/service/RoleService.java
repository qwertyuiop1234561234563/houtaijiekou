
package com.houtai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houtai.entity.PageResult;
import com.houtai.entity.Role;
import com.houtai.entity.RolePageParams;
import java.util.List;

public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     */
    PageResult<Role> getRoleList(RolePageParams params);

    /**
     * 添加角色
     */
    Long addRole(Role role);

    /**
     * 更新角色
     */
    boolean updateRole(Long id, Role role);

    /**
     * 删除角色
     */
    boolean deleteRole(Long id);

    /**
     * 批量删除角色
     */
    boolean batchDeleteRoles(List<Long> ids);

    /**
     * 获取所有权限列表
     */
    List<String> getAllPermissions();

    /**
     * 更新角色权限
     */
    boolean updateRolePermissions(Long roleId, List<String> permissions);
}