package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_coupon")
public class UserCoupon extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券模板ID
     */
    private Long couponId;

    /**
     * 优惠券名称（冗余）
     */
    private String couponName;

    /**
     * 优惠券类型（冗余）
     */
    private Integer couponType;

    /**
     * 优惠金额（冗余）
     */
    private BigDecimal discountAmount;

    /**
     * 折扣比例（冗余）
     */
    private BigDecimal discountRate;

    /**
     * 使用门槛（冗余）
     */
    private BigDecimal minAmount;

    /**
     * 有效开始时间
     */
    private LocalDateTime validStartTime;

    /**
     * 有效结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 状态: 0-未使用 1-已使用 2-已过期
     */
    private Integer status;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * 使用订单ID
     */
    private Long usedOrderId;

    /**
     * 来源: 1-领取 2-签到 3-活动 4-系统发放
     */
    private Integer source;
}
