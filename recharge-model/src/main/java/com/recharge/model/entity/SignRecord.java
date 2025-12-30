package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 签到记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sign_record")
public class SignRecord extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 签到日期
     */
    private LocalDate signDate;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 奖励类型: 1-积分 2-余额 3-优惠券
     */
    private Integer rewardType;

    /**
     * 奖励金额/积分
     */
    private BigDecimal rewardAmount;

    /**
     * 奖励优惠券ID
     */
    private Long rewardCouponId;

    /**
     * 备注
     */
    private String remark;
}
