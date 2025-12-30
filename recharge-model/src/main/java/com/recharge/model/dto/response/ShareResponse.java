package com.recharge.model.dto.response;

import lombok.Data;

/**
 * 分享信息响应
 */
@Data
public class ShareResponse {

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请链接
     */
    private String inviteUrl;

    /**
     * 二维码图片（Base64）
     */
    private String qrCodeImage;

    /**
     * 分享海报（Base64）
     */
    private String posterImage;

    /**
     * 分享标题
     */
    private String shareTitle;

    /**
     * 分享描述
     */
    private String shareDesc;

    /**
     * 分享图片URL
     */
    private String shareImage;

    /**
     * 已邀请人数
     */
    private Integer inviteCount;

    /**
     * 累计佣金
     */
    private String totalCommission;
}
