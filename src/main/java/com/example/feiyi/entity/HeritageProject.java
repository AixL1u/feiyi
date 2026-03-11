package com.example.feiyi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("heritage_project")
public class HeritageProject {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Long categoryId;
    private String level;
    private String region;
    private String description;
    private String content;
    private String coverUrl;
    private String images;
    private String videoUrl;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer status;
    private Integer viewCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String categoryName;
}
