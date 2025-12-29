package com.recharge.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 支付订单请求DTO
 */
@Data
@Schema(description = "支付订单请求")
public class PayOrderRequest {

    @Schema(description = "支付方式: 1-余额 2-支付宝 3-微信", example = "1")
    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    @Schema(description = "支付密码（余额支付时必填）")
    private String payPassword;
}
