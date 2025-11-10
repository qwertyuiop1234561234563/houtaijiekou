package com.houtai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.houtai.entity.PageResult;
import com.houtai.entity.Result;
import com.houtai.entity.User;
import com.houtai.dto.LoginDTO;
import com.houtai.entity.PageParams;
import com.houtai.service.UserService;
import com.houtai.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = userService.login(loginDTO);
            return Result.success(token);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        try {
            User user = userService.getCurrentUserInfo();
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }



    @GetMapping("/list")
    public Result<PageResult<UserVO>> getUserList(PageParams params) {
        PageResult<UserVO> result = userService.getUserList(params);
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result<Long> addUser(@RequestBody User user) {
        Long id = userService.addUser(user);
        return Result.success(id);
    }

    @PostMapping("/update")
    public Result<Void> updateUser(@RequestBody User user) {
        boolean success = userService.updateUser(user.getId(), user);
        return success ? Result.success() : Result.error("更新失败");
    }

    @PostMapping("/delete")
    public Result deleteUser(@RequestBody User user) {
        boolean success = userService.deleteUser(user.getId());
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}