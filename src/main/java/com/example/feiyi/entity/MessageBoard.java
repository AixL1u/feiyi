package com.example.feiyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message_board")
public class MessageBoard {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String nickname;
    private String content;
    private String reply;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
