package com.recharge.web.controller;

import com.recharge.common.result.Result;
import com.recharge.model.dto.request.DistributionUpgradeRequest;
import com.recharge.model.dto.response.DistributionLevelResponse;
import com.recharge.service.distribution.DistributionService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分销中心控制器
 */
@Tag(name = "分销中心", description = "分销等级、升级、佣金统计")
@RestController
@RequestMapping("/api/v1/distribution")
@RequiredArgsConstructor
public class DistributionController {

    private final DistributionService distributionService;

    @Operation(summary = "获取分销中心数据")
    @GetMapping("/center")
    public Result<DistributionService.DistributionCenterResponse> getDistributionCenter(@CurrentUser Long userId) {
        return Result.success(distributionService.getDistributionCenter(userId));
    }

    @Operation(summary = "获取分销等级列表")
    @GetMapping("/levels")
    public Result<List<DistributionLevelResponse>> getLevelList(@CurrentUser Long userId) {
        return Result.success(distributionService.getLevelList(userId));
    }

    @Operation(summary = "升级分销等级")
    @PostMapping("/upgrade")
    public Result<Void> upgradeLevel(@CurrentUser Long userId,
                                      @Valid @RequestBody DistributionUpgradeRequest request) {
        distributionService.upgradLevel(userId, request);
        return Result.success();
    }
}
