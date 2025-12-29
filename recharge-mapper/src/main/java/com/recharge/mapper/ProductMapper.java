package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 产品Mapper - 适配原表 cloud_times_api_huafei
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 基础CRUD由MyBatis-Plus BaseMapper提供
    // 原表 cloud_times_api_huafei 无销量字段
}
