package com.recharge.model.dto.request;

import lombok.Data;

/**
 * 开店请求
 */
@Data
public class OpenStoreRequest {

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺描述
     */
    private String storeDescription;

    /**
     * 支付密码
     */
    private String payPassword;
}
