package com.recharge.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 签到结果响应
 */
@Data
public class SignResultResponse {

    /**
     * 签到是否成功
     */
    private Boolean success;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 奖励类型: 1-积分 2-余额 3-优惠券
     */
    private Integer rewardType;

    /**
     * 奖励金额
     */
    private BigDecimal rewardAmount;

    /**
     * 奖励优惠券ID
     */
    private Long rewardCouponId;

    /**
     * 奖励优惠券名称
     */
    private String rewardCouponName;

    /**
     * 是否获得额外奖励（连续7天）
     */
    private Boolean bonusReward;

    /**
     * 额外奖励金额
     */
    private BigDecimal bonusAmount;

    /**
     * 提示消息
     */
    private String message;
}
