package com.recharge.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单号生成器
 */
public class OrderNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成订单号
     * 格式: 前缀 + 时间戳 + 4位随机数
     *
     * @param prefix 前缀
     * @return 订单号
     */
    public static String generate(String prefix) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return prefix + timestamp + random;
    }

    /**
     * 生成订单号（默认前缀O）
     *
     * @return 订单号
     */
    public static String generate() {
        return generate("O");
    }

    /**
     * 生成充值单号
     *
     * @return 充值单号
     */
    public static String generateRechargeNo() {
        return generate("R");
    }

    /**
     * 生成提现单号
     *
     * @return 提现单号
     */
    public static String generateWithdrawNo() {
        return generate("W");
    }

    /**
     * 生成交易流水号
     *
     * @return 流水号
     */
    public static String generateFlowNo() {
        return generate("F");
    }
}
