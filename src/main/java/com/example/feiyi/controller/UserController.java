package com.example.feiyi.controller;

import com.example.feiyi.common.Result;
import com.example.feiyi.entity.User;
import com.example.feiyi.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user, Authentication auth) {
        if (auth == null) return Result.error(401, "请先登录");
        Long userId = (Long) auth.getPrincipal();
        User u = new User();
        u.setId(userId);
        u.setNickname(user.getNickname());
        u.setAvatar(user.getAvatar());
        u.setPhone(user.getPhone());
        u.setEmail(user.getEmail());
        userMapper.updateById(u);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody PasswordRequest request, Authentication auth) {
        if (auth == null) return Result.error(401, "请先登录");
        Long userId = (Long) auth.getPrincipal();
        User user = userMapper.selectById(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return Result.error("原密码错误");
        }
        User u = new User();
        u.setId(userId);
        u.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(u);
        return Result.success("密码修改成功");
    }

    @lombok.Data
    public static class PasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
