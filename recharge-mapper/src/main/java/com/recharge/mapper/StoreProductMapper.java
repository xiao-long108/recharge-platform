package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.StoreProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺商品Mapper
 */
@Mapper
public interface StoreProductMapper extends BaseMapper<StoreProduct> {
}
