package com.recharge.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户信息响应DTO
 */
@Data
@Schema(description = "用户信息响应")
public class UserInfoResponse {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "账户余额")
    private BigDecimal balance;

    @Schema(description = "佣金余额")
    private BigDecimal commissionBalance;

    @Schema(description = "认证状态")
    private Integer authStatus;

    @Schema(description = "认证状态描述")
    private String authStatusDesc;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "用户等级")
    private Integer level;

    @Schema(description = "是否设置支付密码")
    private Boolean hasPayPassword;

    @Schema(description = "是否有店铺")
    private Boolean hasStore;

    @Schema(description = "店铺ID")
    private Long storeId;
}
