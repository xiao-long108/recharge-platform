package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 分销等级实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_distribution_level")
public class DistributionLevel extends BaseEntity {

    /**
     * 等级名称
     */
    private String name;

    /**
     * 等级值
     */
    private Integer level;

    /**
     * 等级图标
     */
    private String icon;

    /**
     * 升级价格
     */
    private BigDecimal price;

    /**
     * 一级佣金比例
     */
    private BigDecimal commissionRate1;

    /**
     * 二级佣金比例
     */
    private BigDecimal commissionRate2;

    /**
     * 三级佣金比例
     */
    private BigDecimal commissionRate3;

    /**
     * 等级特权说明（JSON）
     */
    private String privileges;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;
}
