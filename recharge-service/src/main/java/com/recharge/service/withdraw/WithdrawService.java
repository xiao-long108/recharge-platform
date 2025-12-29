package com.recharge.service.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.model.entity.WithdrawRecord;

import java.math.BigDecimal;

/**
 * 提现服务接口
 */
public interface WithdrawService {

    /**
     * 申请提现
     *
     * @param userId        用户ID
     * @param bankAccountId 银行账户ID
     * @param amount        提现金额
     * @param withdrawType  提现类型: 1-余额 2-佣金
     * @param payPassword   支付密码
     * @return 提现记录
     */
    WithdrawRecord applyWithdraw(Long userId, Long bankAccountId, BigDecimal amount,
                                  Integer withdrawType, String payPassword);

    /**
     * 分页查询提现记录
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<WithdrawRecord> pageRecords(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取提现详情
     *
     * @param userId     用户ID
     * @param withdrawId 提现ID
     * @return 提现记录
     */
    WithdrawRecord getWithdrawDetail(Long userId, Long withdrawId);

    /**
     * 取消提现
     *
     * @param userId     用户ID
     * @param withdrawId 提现ID
     */
    void cancelWithdraw(Long userId, Long withdrawId);
}
