package com.recharge.service.index;

import com.recharge.model.dto.response.IndexResponse;

/**
 * 首页服务接口
 */
public interface IndexService {

    /**
     * 获取首页数据
     *
     * @param userId 用户ID（可为空）
     * @return 首页数据
     */
    IndexResponse getIndexData(Long userId);
}
