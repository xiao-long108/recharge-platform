package com.recharge.service.yuebao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.model.entity.YuebaoAccount;
import com.recharge.model.entity.YuebaoRecord;

import java.math.BigDecimal;

/**
 * 余额宝服务接口
 */
public interface YuebaoService {

    /**
     * 获取账户信息
     *
     * @param userId 用户ID
     * @return 余额宝账户
     */
    YuebaoAccount getAccount(Long userId);

    /**
     * 转入余额宝
     *
     * @param userId      用户ID
     * @param amount      金额
     * @param payPassword 支付密码
     */
    void transferIn(Long userId, BigDecimal amount, String payPassword);

    /**
     * 转出余额宝
     *
     * @param userId      用户ID
     * @param amount      金额
     * @param payPassword 支付密码
     */
    void transferOut(Long userId, BigDecimal amount, String payPassword);

    /**
     * 分页查询交易记录
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<YuebaoRecord> pageRecords(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 计算每日收益（定时任务调用）
     */
    void calculateDailyIncome();
}
