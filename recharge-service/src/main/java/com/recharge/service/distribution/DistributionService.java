package com.recharge.service.distribution;

import com.recharge.model.dto.request.DistributionUpgradeRequest;
import com.recharge.model.dto.response.DistributionLevelResponse;
import com.recharge.model.entity.UserDistribution;

import java.util.List;

/**
 * 分销服务接口
 */
public interface DistributionService {

    /**
     * 获取分销等级列表
     *
     * @param userId 用户ID
     * @return 等级列表
     */
    List<DistributionLevelResponse> getLevelList(Long userId);

    /**
     * 获取用户当前分销等级
     *
     * @param userId 用户ID
     * @return 用户分销等级
     */
    UserDistribution getUserDistribution(Long userId);

    /**
     * 升级分销等级
     *
     * @param userId  用户ID
     * @param request 升级请求
     */
    void upgradLevel(Long userId, DistributionUpgradeRequest request);

    /**
     * 获取用户分销中心数据
     *
     * @param userId 用户ID
     * @return 分销中心数据
     */
    DistributionCenterResponse getDistributionCenter(Long userId);

    /**
     * 分销中心响应
     */
    record DistributionCenterResponse(
            String levelName,
            Integer level,
            String levelIcon,
            java.math.BigDecimal commissionRate1,
            java.math.BigDecimal commissionRate2,
            java.math.BigDecimal commissionRate3,
            java.math.BigDecimal totalCommission,
            java.math.BigDecimal todayCommission,
            Integer teamCount,
            Integer directCount
    ) {}
}
