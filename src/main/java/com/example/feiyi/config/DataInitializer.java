package com.example.feiyi.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.feiyi.entity.User;
import com.example.feiyi.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUser("admin", "admin123", "系统管理员", "ADMIN");
        initUser("inheritor1", "admin123", "王秀英", "INHERITOR");
        initUser("user1", "admin123", "文化爱好者", "USER");
    }

    private void initUser(String username, String password, String nickname, String role) {
        User existing = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (existing == null) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setNickname(nickname);
            user.setRole(role);
            user.setStatus(1);
            userMapper.insert(user);
        }
    }
}
