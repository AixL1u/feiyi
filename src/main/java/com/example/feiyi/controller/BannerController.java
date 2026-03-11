package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.Banner;
import com.example.feiyi.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerMapper bannerMapper;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getStatus, 1)
                        .orderByAsc(Banner::getSortOrder)));
    }
}
