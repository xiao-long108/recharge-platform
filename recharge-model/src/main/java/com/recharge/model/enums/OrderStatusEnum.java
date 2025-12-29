package com.recharge.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    PENDING_PAY(0, "待支付"),
    PAYING(1, "支付中"),
    RECHARGING(2, "充值中"),
    SUCCESS(3, "充值成功"),
    FAILED(4, "充值失败"),
    CANCELLED(5, "已取消"),
    REFUNDED(6, "已退款");

    private final Integer code;
    private final String desc;

    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
