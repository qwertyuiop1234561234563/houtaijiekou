
package com.houtai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.houtai.entity.PageResult;
import com.houtai.entity.Role;
import com.houtai.entity.RolePageParams;
import com.houtai.mapper.RoleMapper;
import com.houtai.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final StringRedisTemplate stringRedisTemplate;

    // 预定义的权限列表
    private static final List<String> ALL_PERMISSIONS = Arrays.asList(
            "user:add", "user:edit", "user:delete", "user:view",
            "role:add", "role:edit", "role:delete", "role:view",
            "*"
    );

    @Override
    public PageResult<Role> getRoleList(RolePageParams params) {
        log.info("查询角色列表: {}", params);

        // 使用 PageHelper 开始分页
        com.github.pagehelper.Page<Role> page = PageHelper.startPage(
                params.getPage(), params.getPageSize());

        // 执行查询
        List<Role> roleList = this.baseMapper.selectRoleList(params);

        // 处理权限字段
        roleList.forEach(role -> {
            if (StringUtils.hasText(role.getPermissionsStr())) {
                List<String> permissions = Arrays.stream(role.getPermissionsStr().split(","))
                        .collect(Collectors.toList());
                role.setPermissions(permissions);
            }
        });

        // 构建返回结果
        PageResult<Role> result = new PageResult<>();
        result.setList(roleList);
        result.setTotal(page.getTotal());
        result.setPage(params.getPage());
        result.setPageSize(params.getPageSize());
        result.setPages(page.getPages());

        return result;
    }

    @Override
    @Transactional
    public Long addRole(Role role) {
        log.info("添加角色: {}", role.getName());

        // 检查角色编码是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, role.getCode());
        Long count = this.baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("角色编码已存在");
        }

        // 处理权限字段
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            role.setPermissionsStr(String.join(",", role.getPermissions()));
        }

        this.save(role);
        log.info("角色添加成功，ID: {}", role.getId());
        return role.getId();
    }

    @Override
    @Transactional
    public boolean updateRole(Long id, Role role) {
        log.info("更新角色: {}", id);

        role.setId(id);

        // 检查角色编码是否与其他角色重复
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, role.getCode())
                .ne(Role::getId, id);
        Long count = this.baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("角色编码已存在");
        }

        // 处理权限字段
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            role.setPermissionsStr(String.join(",", role.getPermissions()));
        } else {
            role.setPermissionsStr("");
        }

        boolean result = this.updateById(role);
        if (result) {
            log.info("角色更新成功: {}", id);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean deleteRole(Long id) {
        log.info("删除角色: {}", id);

        // 检查角色是否被用户使用（这里需要根据你的业务逻辑实现）
        // if (isRoleInUse(id)) {
        //     throw new RuntimeException("该角色已被使用，无法删除");
        // }

        boolean result = this.removeById(id);
        if (result) {
            log.info("角色删除成功: {}", id);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean batchDeleteRoles(List<Long> ids) {
        log.info("批量删除角色: {}", ids);

        // 检查角色是否被使用
        // for (Long id : ids) {
        //     if (isRoleInUse(id)) {
        //         throw new RuntimeException("角色ID " + id + " 已被使用，无法删除");
        //     }
        // }

        boolean result = this.removeByIds(ids);
        if (result) {
            log.info("批量删除角色成功: {}", ids);
        }
        return result;
    }

    @Override
    public List<String> getAllPermissions() {
        return ALL_PERMISSIONS;
    }

    @Override
    @Transactional
    public boolean updateRolePermissions(Long roleId, List<String> permissions) {
        log.info("更新角色权限: {}, permissions: {}", roleId, permissions);

        Role role = this.getById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        if (permissions != null && !permissions.isEmpty()) {
            role.setPermissionsStr(String.join(",", permissions));
            role.setPermissions(permissions);
        } else {
            role.setPermissionsStr("");
            role.setPermissions(null);
        }

        boolean result = this.updateById(role);
        if (result) {
            log.info("角色权限更新成功: {}", roleId);
        }
        return result;
    }

    /**
     * 检查角色是否被用户使用（需要根据你的业务实现）
     */
    private boolean isRoleInUse(Long roleId) {
        // 这里需要查询用户表，检查是否有用户使用了这个角色
        // 示例：return userMapper.countByRoleId(roleId) > 0;
        return false;
    }
}