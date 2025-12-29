package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 余额宝交易记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_yuebao_record")
public class YuebaoRecord extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 类型: 1-转入 2-转出 3-收益
     */
    private Integer recordType;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 变动前余额
     */
    private BigDecimal balanceBefore;

    /**
     * 变动后余额
     */
    private BigDecimal balanceAfter;

    /**
     * 备注
     */
    private String remark;
}
