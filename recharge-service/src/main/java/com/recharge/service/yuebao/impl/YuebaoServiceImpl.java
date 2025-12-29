package com.recharge.service.yuebao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.enums.ResultCodeEnum;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.YuebaoAccountMapper;
import com.recharge.mapper.YuebaoRecordMapper;
import com.recharge.model.entity.YuebaoAccount;
import com.recharge.model.entity.YuebaoRecord;
import com.recharge.service.user.UserService;
import com.recharge.service.yuebao.YuebaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 余额宝服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YuebaoServiceImpl implements YuebaoService {

    private final YuebaoAccountMapper yuebaoAccountMapper;
    private final YuebaoRecordMapper yuebaoRecordMapper;
    private final UserService userService;

    // 默认年化利率
    private static final BigDecimal DEFAULT_ANNUAL_RATE = new BigDecimal("0.0365");

    @Override
    public YuebaoAccount getAccount(Long userId) {
        YuebaoAccount account = yuebaoAccountMapper.selectByUserId(userId);
        if (account == null) {
            // 自动创建账户
            account = createAccount(userId);
        }
        return account;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferIn(Long userId, BigDecimal amount, String payPassword) {
        // 验证金额
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCodeEnum.YUEBAO_TRANSFER_AMOUNT_ERROR);
        }

        // 验证支付密码
        if (!userService.verifyPayPassword(userId, payPassword)) {
            throw new BusinessException(ResultCodeEnum.USER_PAY_PASSWORD_ERROR);
        }

        // 扣减用户余额
        userService.deductBalance(userId, amount, null, "转入余额宝");

        // 获取或创建余额宝账户
        YuebaoAccount account = getAccount(userId);
        BigDecimal balanceBefore = account.getBalance();

        // 增加余额宝余额
        yuebaoAccountMapper.addBalance(userId, amount);

        // 记录流水
        createRecord(userId, 1, amount, balanceBefore, balanceBefore.add(amount), "余额转入");

        log.info("余额宝转入成功: userId={}, amount={}", userId, amount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferOut(Long userId, BigDecimal amount, String payPassword) {
        // 验证金额
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCodeEnum.YUEBAO_TRANSFER_AMOUNT_ERROR);
        }

        // 验证支付密码
        if (!userService.verifyPayPassword(userId, payPassword)) {
            throw new BusinessException(ResultCodeEnum.USER_PAY_PASSWORD_ERROR);
        }

        // 获取余额宝账户
        YuebaoAccount account = yuebaoAccountMapper.selectByUserId(userId);
        if (account == null || account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(ResultCodeEnum.YUEBAO_BALANCE_INSUFFICIENT);
        }

        BigDecimal balanceBefore = account.getBalance();

        // 扣减余额宝余额
        int rows = yuebaoAccountMapper.deductBalance(userId, amount);
        if (rows == 0) {
            throw new BusinessException(ResultCodeEnum.YUEBAO_BALANCE_INSUFFICIENT);
        }

        // 增加用户余额
        userService.addBalance(userId, amount, null, "余额宝转出");

        // 记录流水
        createRecord(userId, 2, amount, balanceBefore, balanceBefore.subtract(amount), "转出至余额");

        log.info("余额宝转出成功: userId={}, amount={}", userId, amount);
    }

    @Override
    public Page<YuebaoRecord> pageRecords(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<YuebaoRecord> wrapper = new LambdaQueryWrapper<YuebaoRecord>()
                .eq(YuebaoRecord::getUserId, userId)
                .orderByDesc(YuebaoRecord::getCreatedTime);

        return yuebaoRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    @Transactional(rollbackFor = Exception.class)
    public void calculateDailyIncome() {
        log.info("开始计算余额宝每日收益...");

        // 日利率 = 年化利率 / 365
        BigDecimal dailyRate = DEFAULT_ANNUAL_RATE.divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);

        // 分批处理所有账户
        int pageSize = 1000;
        int offset = 0;
        int totalCount = 0;

        while (true) {
            List<YuebaoAccount> accounts = yuebaoAccountMapper.selectBatch(offset, pageSize);
            if (accounts.isEmpty()) {
                break;
            }

            for (YuebaoAccount account : accounts) {
                if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    // 计算收益
                    BigDecimal income = account.getBalance()
                            .multiply(dailyRate)
                            .setScale(4, RoundingMode.HALF_UP);

                    if (income.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal balanceBefore = account.getBalance();

                        // 更新账户
                        yuebaoAccountMapper.updateIncome(account.getId(), income);

                        // 记录流水
                        createRecord(account.getUserId(), 3, income, balanceBefore,
                                balanceBefore.add(income), "每日收益");

                        totalCount++;
                    }
                }
            }

            offset += pageSize;
        }

        log.info("余额宝每日收益计算完成，共处理{}个账户", totalCount);
    }

    private YuebaoAccount createAccount(Long userId) {
        YuebaoAccount account = new YuebaoAccount();
        account.setUserId(userId);
        account.setBalance(BigDecimal.ZERO);
        account.setTotalIncome(BigDecimal.ZERO);
        account.setYesterdayIncome(BigDecimal.ZERO);
        account.setAnnualRate(DEFAULT_ANNUAL_RATE);
        account.setStatus(1);
        account.setDeleted(0);
        yuebaoAccountMapper.insert(account);
        return account;
    }

    private void createRecord(Long userId, Integer type, BigDecimal amount,
                              BigDecimal before, BigDecimal after, String remark) {
        YuebaoRecord record = new YuebaoRecord();
        record.setUserId(userId);
        record.setRecordType(type);
        record.setAmount(amount);
        record.setBalanceBefore(before);
        record.setBalanceAfter(after);
        record.setRemark(remark);
        record.setDeleted(0);
        yuebaoRecordMapper.insert(record);
    }
}
