package com.recharge.web.controller;

import com.recharge.common.result.Result;
import com.recharge.model.dto.response.SignResultResponse;
import com.recharge.model.dto.response.SignStatusResponse;
import com.recharge.service.sign.SignService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 签到控制器
 */
@Tag(name = "签到管理", description = "签到状态、执行签到")
@RestController
@RequestMapping("/api/v1/sign")
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    @Operation(summary = "获取签到状态")
    @GetMapping("/status")
    public Result<SignStatusResponse> getSignStatus(@CurrentUser Long userId) {
        return Result.success(signService.getSignStatus(userId));
    }

    @Operation(summary = "执行签到")
    @PostMapping
    public Result<SignResultResponse> doSign(@CurrentUser Long userId) {
        return Result.success(signService.doSign(userId));
    }

    @Operation(summary = "检查今日是否已签到")
    @GetMapping("/check")
    public Result<Boolean> hasSignedToday(@CurrentUser Long userId) {
        return Result.success(signService.hasSignedToday(userId));
    }
}
