package com.recharge.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
@AllArgsConstructor
public enum PayTypeEnum {

    BALANCE(1, "余额支付"),
    ALIPAY(2, "支付宝"),
    WECHAT(3, "微信支付");

    private final Integer code;
    private final String desc;

    public static PayTypeEnum getByCode(Integer code) {
        for (PayTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
