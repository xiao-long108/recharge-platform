package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 余额宝账户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_yuebao_account")
public class YuebaoAccount extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 余额宝余额
     */
    private BigDecimal balance;

    /**
     * 累计收益
     */
    private BigDecimal totalIncome;

    /**
     * 昨日收益
     */
    private BigDecimal yesterdayIncome;

    /**
     * 当前年化利率
     */
    private BigDecimal annualRate;

    /**
     * 状态: 0-冻结 1-正常
     */
    private Integer status;
}
