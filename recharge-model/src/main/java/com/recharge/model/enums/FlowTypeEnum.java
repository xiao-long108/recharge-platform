package com.recharge.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流水类型枚举
 */
@Getter
@AllArgsConstructor
public enum FlowTypeEnum {

    RECHARGE(1, "充值"),
    CONSUME(2, "消费"),
    REFUND(3, "退款"),
    WITHDRAW(4, "提现"),
    COMMISSION(5, "佣金收入"),
    YUEBAO_IN(6, "余额宝转入"),
    YUEBAO_OUT(7, "余额宝转出");

    private final Integer code;
    private final String desc;

    public static FlowTypeEnum getByCode(Integer code) {
        for (FlowTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
