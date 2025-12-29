package com.recharge.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 提现请求
 */
@Data
public class WithdrawRequest {

    /**
     * 银行账户ID
     */
    private Long bankAccountId;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 提现类型: 1-余额 2-佣金
     */
    private Integer withdrawType;

    /**
     * 支付密码
     */
    private String payPassword;
}
