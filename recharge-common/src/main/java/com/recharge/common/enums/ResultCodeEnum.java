package com.recharge.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    // 成功
    SUCCESS(200, "操作成功"),

    // 通用错误
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),

    // 用户相关 1xxx
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账户已禁用"),
    USER_PHONE_EXIST(1004, "手机号已注册"),
    USER_BALANCE_INSUFFICIENT(1005, "余额不足"),
    USER_COMMISSION_INSUFFICIENT(1006, "佣金余额不足"),
    USER_PAY_PASSWORD_ERROR(1007, "支付密码错误"),
    USER_PAY_PASSWORD_NOT_SET(1008, "请先设置支付密码"),
    USER_NOT_AUTH(1009, "请先完成实名认证"),

    // 订单相关 2xxx
    ORDER_NOT_EXIST(2001, "订单不存在"),
    ORDER_STATUS_ERROR(2002, "订单状态异常"),
    ORDER_PAY_TIMEOUT(2003, "订单支付超时"),
    ORDER_ALREADY_PAID(2004, "订单已支付"),
    ORDER_ALREADY_CANCELLED(2005, "订单已取消"),

    // 提现相关 3xxx
    WITHDRAW_AMOUNT_MIN(3001, "提现金额不能低于最低限额"),
    WITHDRAW_AMOUNT_MAX(3002, "提现金额超过最大限额"),
    WITHDRAW_BALANCE_INSUFFICIENT(3003, "可提现余额不足"),
    BANK_ACCOUNT_NOT_EXIST(3004, "银行账户不存在"),

    // 店铺相关 4xxx
    STORE_NOT_EXIST(4001, "店铺不存在"),
    STORE_ALREADY_EXIST(4002, "您已开通店铺"),
    STORE_STATUS_ERROR(4003, "店铺状态异常"),
    STORE_PRODUCT_NOT_EXIST(4004, "商品不存在"),

    // 余额宝相关 5xxx
    YUEBAO_BALANCE_INSUFFICIENT(5001, "余额宝余额不足"),
    YUEBAO_TRANSFER_AMOUNT_ERROR(5002, "转账金额必须大于0"),

    // 团队相关 6xxx
    INVITE_CODE_NOT_EXIST(6001, "邀请码不存在"),
    INVITE_SELF_ERROR(6002, "不能邀请自己");

    private final Integer code;
    private final String message;
}
