package com.recharge.service.withdraw.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.enums.ResultCodeEnum;
import com.recharge.common.exception.BusinessException;
import com.recharge.common.utils.OrderNoGenerator;
import com.recharge.mapper.UserMapper;
import com.recharge.mapper.WithdrawRecordMapper;
import com.recharge.model.entity.User;
import com.recharge.model.entity.WithdrawRecord;
import com.recharge.model.enums.WithdrawStatusEnum;
import com.recharge.service.bank.BankAccountService;
import com.recharge.service.user.UserService;
import com.recharge.service.withdraw.WithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 提现服务实现
 * 注意：此服务使用的 t_withdraw_record 表可能需要在原数据库中创建
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {

    private final WithdrawRecordMapper withdrawRecordMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final BankAccountService bankAccountService;

    // 手续费率
    private static final BigDecimal FEE_RATE = new BigDecimal("0.006");
    // 最低提现金额
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("100");
    // 最高提现金额
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WithdrawRecord applyWithdraw(Long userId, Long bankAccountId, BigDecimal amount,
                                         Integer withdrawType, String payPassword) {
        // 验证金额
        if (amount.compareTo(MIN_AMOUNT) < 0) {
            throw new BusinessException(ResultCodeEnum.WITHDRAW_AMOUNT_MIN);
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new BusinessException(ResultCodeEnum.WITHDRAW_AMOUNT_MAX);
        }

        // 验证支付密码
        if (!userService.verifyPayPassword(userId, payPassword)) {
            throw new BusinessException(ResultCodeEnum.USER_PAY_PASSWORD_ERROR);
        }

        // 验证银行账户
        bankAccountService.getAccount(userId, bankAccountId);

        // 获取用户信息
        User user = userService.getById(userId);

        // 检查余额 - 使用原数据库字段: price=余额, income_price=佣金
        BigDecimal availableBalance;
        if (withdrawType == 1) {
            availableBalance = user.getPrice();
        } else {
            availableBalance = user.getIncomePrice();
        }

        if (availableBalance.compareTo(amount) < 0) {
            throw new BusinessException(ResultCodeEnum.WITHDRAW_BALANCE_INSUFFICIENT);
        }

        // 计算手续费
        BigDecimal fee = amount.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualAmount = amount.subtract(fee);

        // 扣减余额
        if (withdrawType == 1) {
            int rows = userMapper.deductBalance(userId, amount);
            if (rows == 0) {
                throw new BusinessException(ResultCodeEnum.WITHDRAW_BALANCE_INSUFFICIENT);
            }
        } else {
            int rows = userMapper.deductCommissionBalance(userId, amount);
            if (rows == 0) {
                throw new BusinessException(ResultCodeEnum.WITHDRAW_BALANCE_INSUFFICIENT);
            }
        }

        // 创建提现记录
        WithdrawRecord record = new WithdrawRecord();
        record.setWithdrawNo(OrderNoGenerator.generateWithdrawNo());
        record.setUserId(userId);
        record.setBankAccountId(bankAccountId);
        record.setAmount(amount);
        record.setFee(fee);
        record.setActualAmount(actualAmount);
        record.setWithdrawType(withdrawType);
        record.setStatus(WithdrawStatusEnum.PENDING.getCode());
        record.setDeleted(0);

        withdrawRecordMapper.insert(record);

        log.info("提现申请成功: userId={}, withdrawNo={}, amount={}", userId, record.getWithdrawNo(), amount);
        return record;
    }

    @Override
    public Page<WithdrawRecord> pageRecords(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<WithdrawRecord> wrapper = new LambdaQueryWrapper<WithdrawRecord>()
                .eq(WithdrawRecord::getUserId, userId)
                .orderByDesc(WithdrawRecord::getCreatedTime);

        return withdrawRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public WithdrawRecord getWithdrawDetail(Long userId, Long withdrawId) {
        WithdrawRecord record = withdrawRecordMapper.selectById(withdrawId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("提现记录不存在");
        }
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelWithdraw(Long userId, Long withdrawId) {
        WithdrawRecord record = getWithdrawDetail(userId, withdrawId);

        // 只有待审核状态可以取消
        if (!WithdrawStatusEnum.PENDING.getCode().equals(record.getStatus())) {
            throw new BusinessException("当前状态不可取消");
        }

        // 退回余额
        if (record.getWithdrawType() == 1) {
            userMapper.addBalance(userId, record.getAmount());
        } else {
            userMapper.addCommissionBalance(userId, record.getAmount());
        }

        // 更新状态
        record.setStatus(WithdrawStatusEnum.CANCELLED.getCode());
        withdrawRecordMapper.updateById(record);

        log.info("取消提现成功: userId={}, withdrawNo={}", userId, record.getWithdrawNo());
    }
}
