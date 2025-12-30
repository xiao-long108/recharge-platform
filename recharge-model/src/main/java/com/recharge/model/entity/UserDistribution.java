package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户分销等级实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_distribution")
public class UserDistribution extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分销等级ID
     */
    private Long levelId;

    /**
     * 当前等级值
     */
    private Integer level;

    /**
     * 升级时间
     */
    private LocalDateTime upgradeTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态: 0-过期 1-有效
     */
    private Integer status;
}
