package com.houtai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houtai.entity.User;
import com.houtai.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查询用户列表（包含角色名称）
     */
    List<UserVO> selectUserListWithRoles(@Param("username") String username);

    /**
     * 根据用户ID查询角色名称
     */
    @Select("SELECT r.name FROM role r WHERE FIND_IN_SET(r.id, #{roles})")
    List<String> selectRoleNamesByUserRoles(@Param("roles") String roles);
}