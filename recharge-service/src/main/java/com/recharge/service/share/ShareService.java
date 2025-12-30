package com.recharge.service.share;

import com.recharge.model.dto.response.ShareResponse;

/**
 * 分享服务接口
 */
public interface ShareService {

    /**
     * 获取分享信息
     *
     * @param userId 用户ID
     * @return 分享信息
     */
    ShareResponse getShareInfo(Long userId);

    /**
     * 生成邀请二维码
     *
     * @param userId 用户ID
     * @return 二维码Base64
     */
    String generateQrCode(Long userId);

    /**
     * 生成分享海报
     *
     * @param userId 用户ID
     * @return 海报Base64
     */
    String generatePoster(Long userId);
}
