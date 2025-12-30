package com.recharge.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券响应
 */
@Data
public class CouponResponse {

    /**
     * 用户优惠券ID
     */
    private Long id;

    /**
     * 优惠券模板ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 类型: 1-满减券 2-折扣券 3-无门槛券
     */
    private Integer couponType;

    /**
     * 类型名称
     */
    private String couponTypeName;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 折扣比例
     */
    private BigDecimal discountRate;

    /**
     * 使用门槛金额
     */
    private BigDecimal minAmount;

    /**
     * 使用说明
     */
    private String description;

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
     * 状态名称
     */
    private String statusName;

    /**
     * 是否可用
     */
    private Boolean available;

    /**
     * 剩余有效天数
     */
    private Integer remainDays;
}
