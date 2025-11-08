package com.houtai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.houtai.entity.User;

import com.houtai.entity.PageParams;
import com.houtai.dto.LoginDTO;

public interface UserService extends IService<User> {
    String login(LoginDTO loginDTO);
    User getUserInfo(String token);
    Page<User> getUserList(PageParams params);
    Long addUser(User user);
    boolean updateUser(Long id, User user);
    boolean deleteUser(Long id);

    User getCurrentUserInfo();
}