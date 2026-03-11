package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.ProductCategory;
import com.example.feiyi.mapper.ProductCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(categoryMapper.selectList(
                new LambdaQueryWrapper<ProductCategory>().orderByAsc(ProductCategory::getSortOrder)));
    }
}
