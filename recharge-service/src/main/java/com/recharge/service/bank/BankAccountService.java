package com.recharge.service.bank;

import com.recharge.model.entity.BankAccount;

import java.util.List;

/**
 * 银行账户服务接口
 */
public interface BankAccountService {

    /**
     * 获取银行账户列表
     *
     * @param userId 用户ID
     * @return 账户列表
     */
    List<BankAccount> listAccounts(Long userId);

    /**
     * 添加银行账户
     *
     * @param userId        用户ID
     * @param bankName      银行名称
     * @param accountHolder 持卡人姓名
     * @param accountNumber 银行卡号
     * @param branchName    开户支行
     * @return 银行账户
     */
    BankAccount addAccount(Long userId, String bankName, String accountHolder,
                           String accountNumber, String branchName);

    /**
     * 删除银行账户
     *
     * @param userId    用户ID
     * @param accountId 账户ID
     */
    void deleteAccount(Long userId, Long accountId);

    /**
     * 设为默认账户
     *
     * @param userId    用户ID
     * @param accountId 账户ID
     */
    void setDefault(Long userId, Long accountId);

    /**
     * 获取账户详情
     *
     * @param userId    用户ID
     * @param accountId 账户ID
     * @return 银行账户
     */
    BankAccount getAccount(Long userId, Long accountId);
}
