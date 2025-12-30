package com.recharge.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分销升级请求
 */
@Data
public class DistributionUpgradeRequest {

    /**
     * 目标等级ID
     */
    @NotNull(message = "等级ID不能为空")
    private Long levelId;

    /**
     * 支付密码
     */
    @NotBlank(message = "支付密码不能为空")
    private String payPassword;
}
