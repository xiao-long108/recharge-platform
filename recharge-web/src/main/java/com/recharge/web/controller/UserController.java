package com.recharge.web.controller;

import com.recharge.common.result.Result;
import com.recharge.model.dto.response.UserInfoResponse;
import com.recharge.service.user.UserService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户信息、密码等")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo(@CurrentUser Long userId) {
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@CurrentUser Long userId,
                                        @RequestParam(required = false) String nickname,
                                        @RequestParam(required = false) String avatar) {
        userService.updateUserInfo(userId, nickname, avatar);
        return Result.success();
    }

    @Operation(summary = "修改登录密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@CurrentUser Long userId,
                                        @RequestParam String oldPassword,
                                        @RequestParam String newPassword) {
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "设置支付密码")
    @PostMapping("/pay-password")
    public Result<Void> setPayPassword(@CurrentUser Long userId,
                                        @RequestParam String payPassword) {
        userService.setPayPassword(userId, payPassword);
        return Result.success();
    }
}
