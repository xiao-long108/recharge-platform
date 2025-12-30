package com.recharge.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分销等级响应
 */
@Data
public class DistributionLevelResponse {

    /**
     * 等级ID
     */
    private Long id;

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
     * 等级特权列表
     */
    private List<String> privileges;

    /**
     * 是否当前等级
     */
    private Boolean current;

    /**
     * 是否可升级到此等级
     */
    private Boolean canUpgrade;
}
