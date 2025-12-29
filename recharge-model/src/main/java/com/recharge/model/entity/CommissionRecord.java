package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 佣金记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_commission_record")
public class CommissionRecord extends BaseEntity {

    /**
     * 获得佣金的用户ID
     */
    private Long userId;

    /**
     * 来源用户ID
     */
    private Long fromUserId;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;

    /**
     * 佣金金额
     */
    private BigDecimal commissionAmount;

    /**
     * 佣金层级: 1-一级 2-二级 3-三级
     */
    private Integer commissionLevel;

    /**
     * 状态: 0-待结算 1-已结算 2-已取消
     */
    private Integer status;

    /**
     * 结算时间
     */
    private LocalDateTime settleTime;
}
