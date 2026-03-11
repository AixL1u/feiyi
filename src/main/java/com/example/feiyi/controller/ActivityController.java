package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.Activity;
import com.example.feiyi.entity.ActivityReservation;
import com.example.feiyi.mapper.ActivityMapper;
import com.example.feiyi.mapper.ActivityReservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityReservationMapper reservationMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        Page<Activity> p = new Page<>(page, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Activity::getStatus, 1);
        wrapper.orderByDesc(Activity::getStartTime);
        activityMapper.selectPage(p, wrapper);
        return Result.success(p);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.success(activity);
    }

    @PostMapping("/reserve")
    public Result<?> reserve(@RequestBody ActivityReservation reservation, Authentication auth) {
        if (auth == null) {
            return Result.error(401, "请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        Activity activity = activityMapper.selectById(reservation.getActivityId());
        if (activity == null) {
            return Result.error("活动不存在");
        }
        if (activity.getMaxParticipants() > 0 &&
                activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return Result.error("报名名额已满");
        }
        Long count = reservationMapper.selectCount(
                new LambdaQueryWrapper<ActivityReservation>()
                        .eq(ActivityReservation::getActivityId, reservation.getActivityId())
                        .eq(ActivityReservation::getUserId, userId)
                        .ne(ActivityReservation::getStatus, 3));
        if (count > 0) {
            return Result.error("您已报名该活动");
        }
        reservation.setUserId(userId);
        reservation.setStatus(0);
        reservationMapper.insert(reservation);
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);
        return Result.success("报名成功，等待审核");
    }

    @GetMapping("/my-reservations")
    public Result<?> myReservations(Authentication auth) {
        if (auth == null) {
            return Result.error(401, "请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        return Result.success(reservationMapper.selectList(
                new LambdaQueryWrapper<ActivityReservation>()
                        .eq(ActivityReservation::getUserId, userId)
                        .orderByDesc(ActivityReservation::getCreateTime)));
    }
}
