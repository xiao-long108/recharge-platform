package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_withdraw_record")
public class WithdrawRecord extends BaseEntity {

    /**
     * 提现单号
     */
    private String withdrawNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 银行账户ID
     */
    private Long bankAccountId;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 实际到账金额
     */
    private BigDecimal actualAmount;

    /**
     * 提现类型: 1-余额 2-佣金
     */
    private Integer withdrawType;

    /**
     * 状态: 0-待审核 1-处理中 2-成功 3-失败 4-已取消
     */
    private Integer status;

    /**
     * 审核人ID
     */
    private Long auditUserId;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 失败原因
     */
    private String failReason;
}
