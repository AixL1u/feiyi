package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.MessageBoard;
import com.example.feiyi.mapper.MessageBoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageBoardMapper messageMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        Page<MessageBoard> p = new Page<>(page, size);
        messageMapper.selectPage(p, new LambdaQueryWrapper<MessageBoard>()
                .eq(MessageBoard::getStatus, 1)
                .orderByDesc(MessageBoard::getCreateTime));
        return Result.success(p);
    }

    @PostMapping
    public Result<?> post(@RequestBody MessageBoard message, Authentication auth) {
        if (auth != null) {
            Long userId = (Long) auth.getPrincipal();
            message.setUserId(userId);
        }
        message.setStatus(0);
        messageMapper.insert(message);
        return Result.success("留言提交成功，等待审核");
    }
}
