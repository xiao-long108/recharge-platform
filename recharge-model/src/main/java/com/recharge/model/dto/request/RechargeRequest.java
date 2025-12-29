package com.recharge.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 充值请求DTO
 */
@Data
@Schema(description = "充值请求")
public class RechargeRequest {

    @Schema(description = "产品ID", example = "1")
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @Schema(description = "充值手机号", example = "13800138000")
    @NotBlank(message = "充值手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    @Schema(description = "店铺ID（可选）", example = "1")
    private Long storeId;

    @Schema(description = "优惠券ID（可选）", example = "1")
    private Long couponId;
}
