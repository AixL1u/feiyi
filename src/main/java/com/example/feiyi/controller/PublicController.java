package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.HeritageProject;
import com.example.feiyi.entity.Inheritor;
import com.example.feiyi.entity.Product;
import com.example.feiyi.mapper.HeritageProjectMapper;
import com.example.feiyi.mapper.InheritorMapper;
import com.example.feiyi.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private HeritageProjectMapper projectMapper;
    @Autowired
    private InheritorMapper inheritorMapper;
    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/search")
    public Result<?> search(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        result.put("projects", projectMapper.selectList(
                new LambdaQueryWrapper<HeritageProject>()
                        .eq(HeritageProject::getStatus, 1)
                        .and(w -> w.like(HeritageProject::getTitle, keyword)
                                .or().like(HeritageProject::getDescription, keyword))
                        .last("LIMIT 10")));
        result.put("inheritors", inheritorMapper.selectList(
                new LambdaQueryWrapper<Inheritor>()
                        .eq(Inheritor::getStatus, 1)
                        .and(w -> w.like(Inheritor::getName, keyword)
                                .or().like(Inheritor::getSkills, keyword))
                        .last("LIMIT 10")));
        result.put("products", productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .and(w -> w.like(Product::getName, keyword)
                                .or().like(Product::getDescription, keyword))
                        .last("LIMIT 10")));
        return Result.success(result);
    }
}
