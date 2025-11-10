package com.houtai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houtai.entity.PageResult;
import com.houtai.entity.User;

import com.houtai.entity.PageParams;
import com.houtai.dto.LoginDTO;
import com.houtai.vo.UserVO;

public interface UserService extends IService<User> {
    String login(LoginDTO loginDTO);
    User getUserInfo(String token);
    PageResult<UserVO> getUserList(PageParams params);
    Long addUser(User user);
    boolean updateUser(Long id, User user);
    boolean deleteUser(Long id);
    User getCurrentUserInfo();
}