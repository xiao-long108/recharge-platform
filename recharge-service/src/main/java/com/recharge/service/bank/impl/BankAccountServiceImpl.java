package com.recharge.service.bank.impl;

import com.recharge.common.enums.ResultCodeEnum;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.BankAccountMapper;
import com.recharge.model.entity.BankAccount;
import com.recharge.service.bank.BankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 银行账户服务实现 - 适配原表 cloud_times_api_account
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountMapper bankAccountMapper;

    @Override
    public List<BankAccount> listAccounts(Long userId) {
        return bankAccountMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankAccount addAccount(Long userId, String bankName, String accountHolder,
                                  String accountNumber, String branchName) {
        // 检查是否有默认账户
        BankAccount defaultAccount = bankAccountMapper.selectDefaultByUserId(userId);

        BankAccount account = new BankAccount();
        account.setUserId(userId);
        account.setBankName(bankName);
        account.setAccountName(accountHolder);      // 原字段: account_name
        account.setAccountNumber(accountNumber);
        account.setBankBranch(branchName);          // 原字段: bank_branch
        account.setIsDefault(defaultAccount == null ? 1 : 2); // 原数据库: 1=默认, 2=非默认
        account.setCreateTime(LocalDateTime.now());

        bankAccountMapper.insert(account);

        log.info("添加银行账户成功: userId={}, accountId={}", userId, account.getId());
        return account;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(Long userId, Long accountId) {
        BankAccount account = getAccount(userId, accountId);

        // 逻辑删除 - 使用 deleteTime 字段
        account.setDeleteTime(LocalDateTime.now());
        bankAccountMapper.updateById(account);

        // 如果删除的是默认账户，将第一张卡设为默认
        if (account.getIsDefault() == 1) {
            List<BankAccount> accounts = bankAccountMapper.selectByUserId(userId);
            if (!accounts.isEmpty()) {
                BankAccount first = accounts.get(0);
                first.setIsDefault(1);
                bankAccountMapper.updateById(first);
            }
        }

        log.info("删除银行账户成功: userId={}, accountId={}", userId, accountId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long accountId) {
        BankAccount account = getAccount(userId, accountId);

        // 取消其他默认
        bankAccountMapper.cancelDefault(userId);

        // 设为默认
        account.setIsDefault(1);
        account.setUpdateTime(LocalDateTime.now());
        bankAccountMapper.updateById(account);

        log.info("设置默认银行账户: userId={}, accountId={}", userId, accountId);
    }

    @Override
    public BankAccount getAccount(Long userId, Long accountId) {
        BankAccount account = bankAccountMapper.selectById(accountId);
        if (account == null || !account.getUserId().equals(userId) || account.getDeleteTime() != null) {
            throw new BusinessException(ResultCodeEnum.BANK_ACCOUNT_NOT_EXIST);
        }
        return account;
    }
}
