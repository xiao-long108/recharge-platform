package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 店铺Mapper - 适配原表 cloud_times_api_store
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    /**
     * 根据用户ID查询店铺
     */
    default Store selectByUserId(Long userId) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Store>()
                .eq(Store::getUserId, userId));
    }

    /**
     * 增加店铺余额 (s_price字段)
     */
    @Update("UPDATE cloud_times_api_store SET s_price = s_price + #{amount}, update_time = NOW() WHERE id = #{storeId}")
    int addBalance(@Param("storeId") Long storeId, @Param("amount") BigDecimal amount);

    /**
     * 增加销量
     */
    @Update("UPDATE cloud_times_api_store SET s_sales = s_sales + 1, update_time = NOW() WHERE id = #{storeId}")
    int addSales(@Param("storeId") Long storeId);
}
