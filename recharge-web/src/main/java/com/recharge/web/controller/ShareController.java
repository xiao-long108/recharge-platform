package com.recharge.web.controller;

import com.recharge.common.result.Result;
import com.recharge.model.dto.response.ShareResponse;
import com.recharge.service.share.ShareService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分享邀请控制器
 */
@Tag(name = "分享邀请", description = "邀请码、二维码、分享海报")
@RestController
@RequestMapping("/api/v1/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @Operation(summary = "获取分享信息")
    @GetMapping
    public Result<ShareResponse> getShareInfo(@CurrentUser Long userId) {
        return Result.success(shareService.getShareInfo(userId));
    }

    @Operation(summary = "获取邀请二维码")
    @GetMapping("/qrcode")
    public Result<String> getQrCode(@CurrentUser Long userId) {
        return Result.success(shareService.generateQrCode(userId));
    }

    @Operation(summary = "获取分享海报")
    @GetMapping("/poster")
    public Result<String> getPoster(@CurrentUser Long userId) {
        return Result.success(shareService.generatePoster(userId));
    }
}
