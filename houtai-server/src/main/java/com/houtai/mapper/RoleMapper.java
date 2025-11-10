package com.houtai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houtai.entity.Role;
import com.houtai.entity.RolePageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 分页查询角色列表
     */
    List<Role> selectRoleList(@Param("params") RolePageParams params);

    /**
     * 根据角色编码查询
     */
    Role selectByCode(@Param("code") String code);
}
