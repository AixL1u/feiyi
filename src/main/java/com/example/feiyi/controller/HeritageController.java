package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.HeritageCategory;
import com.example.feiyi.entity.HeritageProject;
import com.example.feiyi.mapper.HeritageCategoryMapper;
import com.example.feiyi.mapper.HeritageProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/heritage")
public class HeritageController {

    @Autowired
    private HeritageProjectMapper projectMapper;

    @Autowired
    private HeritageCategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String level) {
        Page<HeritageProject> p = new Page<>(page, size);
        LambdaQueryWrapper<HeritageProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HeritageProject::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(HeritageProject::getTitle, keyword)
                    .or().like(HeritageProject::getDescription, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(HeritageProject::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq(HeritageProject::getLevel, level);
        }
        wrapper.orderByDesc(HeritageProject::getCreateTime);
        projectMapper.selectPage(p, wrapper);
        return Result.success(p);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        HeritageProject project = projectMapper.selectById(id);
        if (project == null) {
            return Result.error("项目不存在");
        }
        project.setViewCount(project.getViewCount() + 1);
        projectMapper.updateById(project);
        if (project.getCategoryId() != null) {
            HeritageCategory cat = categoryMapper.selectById(project.getCategoryId());
            if (cat != null) {
                project.setCategoryName(cat.getName());
            }
        }
        return Result.success(project);
    }

    @GetMapping("/all")
    public Result<?> all() {
        List<HeritageProject> list = projectMapper.selectList(
                new LambdaQueryWrapper<HeritageProject>().eq(HeritageProject::getStatus, 1)
                        .orderByDesc(HeritageProject::getViewCount));
        return Result.success(list);
    }
}
