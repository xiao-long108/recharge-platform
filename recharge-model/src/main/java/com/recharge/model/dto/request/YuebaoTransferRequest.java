package com.recharge.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 余额宝转入/转出请求
 */
@Data
public class YuebaoTransferRequest {

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 支付密码
     */
    private String payPassword;
}
