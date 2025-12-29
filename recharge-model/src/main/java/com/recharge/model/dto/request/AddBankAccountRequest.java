package com.recharge.model.dto.request;

import lombok.Data;

/**
 * 添加银行账户请求
 */
@Data
public class AddBankAccountRequest {

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 持卡人姓名
     */
    private String accountHolder;

    /**
     * 银行卡号
     */
    private String accountNumber;

    /**
     * 开户支行
     */
    private String branchName;
}
