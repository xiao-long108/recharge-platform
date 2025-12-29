package com.recharge.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum PayStatusEnum {

    UNPAID(0, "未支付"),
    PAYING(1, "支付中"),
    PAID(2, "已支付"),
    REFUNDING(3, "退款中"),
    REFUNDED(4, "已退款");

    private final Integer code;
    private final String desc;

    public static PayStatusEnum getByCode(Integer code) {
        for (PayStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
