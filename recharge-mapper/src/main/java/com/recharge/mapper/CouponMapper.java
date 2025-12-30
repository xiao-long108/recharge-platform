package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券模板Mapper
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
}
