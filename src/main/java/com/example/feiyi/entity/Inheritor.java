package com.example.feiyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inheritor")
public class Inheritor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String avatar;
    private String images;
    private String videoUrl;
    private String gender;
    private Integer birthYear;
    private String level;
    private Long projectId;
    private String region;
    private String bio;
    private String story;
    private String skills;
    private String honors;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String projectName;
}
