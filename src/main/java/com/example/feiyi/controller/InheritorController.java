package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.HeritageProject;
import com.example.feiyi.entity.Inheritor;
import com.example.feiyi.mapper.HeritageProjectMapper;
import com.example.feiyi.mapper.InheritorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inheritor")
public class InheritorController {

    @Autowired
    private InheritorMapper inheritorMapper;

    @Autowired
    private HeritageProjectMapper projectMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword) {
        Page<Inheritor> p = new Page<>(page, size);
        LambdaQueryWrapper<Inheritor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inheritor::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Inheritor::getName, keyword)
                    .or().like(Inheritor::getSkills, keyword));
        }
        wrapper.orderByDesc(Inheritor::getCreateTime);
        inheritorMapper.selectPage(p, wrapper);
        return Result.success(p);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Inheritor inheritor = inheritorMapper.selectById(id);
        if (inheritor == null) {
            return Result.error("传承人不存在");
        }
        if (inheritor.getProjectId() != null) {
            HeritageProject project = projectMapper.selectById(inheritor.getProjectId());
            if (project != null) {
                inheritor.setProjectName(project.getTitle());
            }
        }
        return Result.success(inheritor);
    }
}
