package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.HeritageCategory;
import com.example.feiyi.mapper.HeritageCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private HeritageCategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<?> list() {
        List<HeritageCategory> list = categoryMapper.selectList(
                new LambdaQueryWrapper<HeritageCategory>().orderByAsc(HeritageCategory::getSortOrder));
        return Result.success(list);
    }
}
