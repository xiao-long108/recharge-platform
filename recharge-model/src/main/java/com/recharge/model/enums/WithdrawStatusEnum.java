package com.recharge.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提现状态枚举
 */
@Getter
@AllArgsConstructor
public enum WithdrawStatusEnum {

    PENDING(0, "待审核"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "提现成功"),
    FAILED(3, "提现失败"),
    CANCELLED(4, "已取消");

    private final Integer code;
    private final String desc;

    public static WithdrawStatusEnum getByCode(Integer code) {
        for (WithdrawStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
