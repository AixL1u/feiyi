package com.example.feiyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.feiyi.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
