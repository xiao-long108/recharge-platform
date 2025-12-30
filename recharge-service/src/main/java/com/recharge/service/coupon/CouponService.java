package com.recharge.service.coupon;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.model.dto.response.CouponListResponse;
import com.recharge.model.dto.response.CouponResponse;
import com.recharge.model.entity.UserCoupon;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {

    /**
     * 获取可领取的优惠券列表
     *
     * @param userId 用户ID
     * @return 优惠券列表
     */
    List<CouponListResponse> getAvailableCoupons(Long userId);

    /**
     * 领取优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券模板ID
     */
    void receiveCoupon(Long userId, Long couponId);

    /**
     * 获取我的优惠券列表
     *
     * @param userId 用户ID
     * @param status 状态: null-全部 0-未使用 1-已使用 2-已过期
     * @param page   页码
     * @param size   每页数量
     * @return 优惠券列表
     */
    Page<CouponResponse> getMyCoupons(Long userId, Integer status, Integer page, Integer size);

    /**
     * 获取可用优惠券（用于订单）
     *
     * @param userId     用户ID
     * @param orderAmount 订单金额
     * @return 可用优惠券列表
     */
    List<CouponResponse> getUsableCoupons(Long userId, BigDecimal orderAmount);

    /**
     * 使用优惠券
     *
     * @param userCouponId 用户优惠券ID
     * @param orderId      订单ID
     * @return 优惠金额
     */
    BigDecimal useCoupon(Long userCouponId, Long orderId);

    /**
     * 退还优惠券（订单取消/退款时）
     *
     * @param orderId 订单ID
     */
    void returnCoupon(Long orderId);

    /**
     * 系统发放优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券模板ID
     * @param source   来源
     */
    void grantCoupon(Long userId, Long couponId, Integer source);

    /**
     * 统计用户可用优惠券数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    Integer countAvailable(Long userId);

    /**
     * 根据ID获取用户优惠券
     *
     * @param userCouponId 用户优惠券ID
     * @return 用户优惠券
     */
    UserCoupon getUserCouponById(Long userCouponId);
}
