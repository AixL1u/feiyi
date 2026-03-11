package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.UserAddress;
import com.example.feiyi.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private UserAddressMapper addressMapper;

    @GetMapping("/list")
    public Result<?> list(Authentication auth) {
        if (auth == null) return Result.error(401, "请先登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(addressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)));
    }

    @PostMapping
    public Result<?> save(@RequestBody UserAddress address, Authentication auth) {
        if (auth == null) return Result.error(401, "请先登录");
        Long userId = (Long) auth.getPrincipal();
        address.setUserId(userId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            UserAddress update = new UserAddress();
            update.setIsDefault(0);
            addressMapper.update(update, new LambdaQueryWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId));
        }
        if (address.getId() != null) {
            addressMapper.updateById(address);
        } else {
            addressMapper.insert(address);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, Authentication auth) {
        if (auth == null) return Result.error(401, "请先登录");
        addressMapper.deleteById(id);
        return Result.success();
    }
}
