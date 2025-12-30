package com.recharge.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 可领取优惠券列表响应
 */
@Data
public class CouponListResponse {

    /**
     * 优惠券ID
     */
    private Long id;

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
     * 有效天数
     */
    private Integer validDays;

    /**
     * 有效期开始
     */
    private LocalDateTime startTime;

    /**
     * 有效期结束
     */
    private LocalDateTime endTime;

    /**
     * 剩余数量
     */
    private Integer remainCount;

    /**
     * 是否已领取
     */
    private Boolean received;

    /**
     * 已领取数量
     */
    private Integer receivedCount;

    /**
     * 每人限领数量
     */
    private Integer perUserLimit;
}
