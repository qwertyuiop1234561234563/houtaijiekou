package com.houtai.controller;

import com.houtai.entity.PageResult;
import com.houtai.entity.ApiResponse;
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
    public ApiResponse<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = userService.login(loginDTO);
            return ApiResponse.success(token);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public ApiResponse<User> getUserInfo() {
        try {
            User user = userService.getCurrentUserInfo();
            return ApiResponse.success(user);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }



    @GetMapping("/list")
    public ApiResponse<PageResult<UserVO>> getUserList(PageParams params) {
        PageResult<UserVO> result = userService.getUserList(params);
        return ApiResponse.success(result);
    }

    @PostMapping("/add")
    public ApiResponse<Long> addUser(@RequestBody User user) {
        Long id = userService.addUser(user);
        return ApiResponse.success(id);
    }

    @PostMapping("/update")
    public ApiResponse<Void> updateUser(@RequestBody User user) {
        boolean success = userService.updateUser(user.getId(), user);
        return success ? ApiResponse.success() : ApiResponse.error("更新失败");
    }

    @PostMapping("/delete")
    public ApiResponse deleteUser(@RequestBody User user) {
        boolean success = userService.deleteUser(user.getId());
        return success ? ApiResponse.success("删除成功") : ApiResponse.error("删除失败");
    }
}