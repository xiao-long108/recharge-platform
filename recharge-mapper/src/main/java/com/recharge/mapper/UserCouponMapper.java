package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户优惠券Mapper
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    /**
     * 统计用户已领取某优惠券数量
     */
    @Select("SELECT COUNT(*) FROM t_user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId} AND deleted = 0")
    Integer countUserReceived(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 统计用户可用优惠券数量
     */
    @Select("SELECT COUNT(*) FROM t_user_coupon WHERE user_id = #{userId} AND status = 0 AND valid_end_time > NOW() AND deleted = 0")
    Integer countAvailable(@Param("userId") Long userId);
}
