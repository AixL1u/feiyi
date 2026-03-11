package com.example.feiyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.feiyi.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
