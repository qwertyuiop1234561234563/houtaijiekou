package com.houtai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.houtai.entity.PageResult;
import com.houtai.entity.Role;
import com.houtai.entity.RolePageParams;
import com.houtai.mapper.RoleMapper;
import com.houtai.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public PageResult<Role> getRoleList(RolePageParams params) {
        log.info("查询角色列表: page={}, pageSize={}, name={}, code={}",
                params.getPage(), params.getPageSize(), params.getName(), params.getCode());

        try {
            // 使用 PageHelper 开始分页
            com.github.pagehelper.Page<Role> page = PageHelper.startPage(
                    params.getPage(), params.getPageSize());

            // 创建查询条件
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

            if (StringUtils.hasText(params.getName())) {
                wrapper.like(Role::getName, params.getName().trim());
            }

            if (StringUtils.hasText(params.getCode())) {
                wrapper.like(Role::getCode, params.getCode().trim());
            }

            wrapper.orderByDesc(Role::getCreateTime);

            // 执行查询
            List<Role> roleList = this.list(wrapper);
            log.info("查询到 {} 条角色记录", roleList.size());

            // 处理权限字段 - 将 permission 转换为 permissions 列表
            roleList.forEach(role -> {
                if (StringUtils.hasText(role.getPermissionStr())) {
                    List<String> permissions = Arrays.stream(role.getPermissionStr().split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    role.setPermissions(permissions);
                } else {
                    role.setPermissions(List.of());
                }
                log.debug("角色: {}, 权限: {}", role.getName(), role.getPermissions());
            });

            // 构建返回结果
            PageResult<Role> result = new PageResult<>();
            result.setList(roleList);
            result.setTotal(page.getTotal());
            result.setPage(params.getPage());
            result.setPageSize(params.getPageSize());
            result.setPages(page.getPages());

            log.info("分页结果: total={}, pages={}", result.getTotal(), result.getPages());
            return result;

        } catch (Exception e) {
            log.error("查询角色列表失败", e);
            throw new RuntimeException("查询角色列表失败: " + e.getMessage());
        }
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

        // 处理权限字段 - 使用 permissionStr 而不是 permissionsStr
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            role.setPermissionStr(String.join(",", role.getPermissions()));
        }

        this.save(role);
        log.info("角色添加成功，ID: {}", role.getId());
        return role.getId().longValue();
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
            role.setPermissionStr(String.join(",", role.getPermissions()));
        } else {
            role.setPermissionStr("");
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
        boolean result = this.removeById(id.intValue());
        if (result) {
            log.info("角色删除成功: {}", id);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean batchDeleteRoles(List<Long> ids) {
        log.info("批量删除角色: {}", ids);
        List<Integer> intIds = ids.stream().map(Long::intValue).collect(Collectors.toList());
        boolean result = this.removeByIds(intIds);
        if (result) {
            log.info("批量删除角色成功: {}", ids);
        }
        return result;
    }

    @Override
    public List<String> getAllPermissions() {
        return Arrays.asList(
                "user:add", "user:edit", "user:delete", "user:view",
                "role:add", "role:edit", "role:delete", "role:view", "*"
        );
    }

    @Override
    @Transactional
    public boolean updateRolePermissions(Long roleId, List<String> permissions) {
        log.info("更新角色权限: {}, permissions: {}", roleId, permissions);

        Role role = this.getById(roleId.intValue());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        if (permissions != null && !permissions.isEmpty()) {
            role.setPermissionStr(String.join(",", permissions));
            role.setPermissions(permissions);
        } else {
            role.setPermissionStr("");
            role.setPermissions(null);
        }

        boolean result = this.updateById(role);
        if (result) {
            log.info("角色权限更新成功: {}", roleId);
        }
        return result;
    }
}