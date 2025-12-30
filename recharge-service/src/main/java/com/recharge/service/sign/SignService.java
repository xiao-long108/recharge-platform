package com.recharge.service.sign;

import com.recharge.model.dto.response.SignResultResponse;
import com.recharge.model.dto.response.SignStatusResponse;

/**
 * 签到服务接口
 */
public interface SignService {

    /**
     * 获取签到状态
     *
     * @param userId 用户ID
     * @return 签到状态
     */
    SignStatusResponse getSignStatus(Long userId);

    /**
     * 执行签到
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    SignResultResponse doSign(Long userId);

    /**
     * 检查今日是否已签到
     *
     * @param userId 用户ID
     * @return 是否已签到
     */
    boolean hasSignedToday(Long userId);
}
