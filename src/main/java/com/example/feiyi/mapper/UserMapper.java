package com.example.feiyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.feiyi.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
