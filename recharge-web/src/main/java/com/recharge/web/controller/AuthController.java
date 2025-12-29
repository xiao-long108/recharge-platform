package com.recharge.web.controller;

import com.recharge.common.result.Result;
import com.recharge.model.dto.request.LoginRequest;
import com.recharge.model.dto.request.RegisterRequest;
import com.recharge.model.dto.response.LoginResponse;
import com.recharge.service.auth.AuthService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "登录、注册等")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated RegisterRequest request) {
        authService.register(request);
        return Result.success();
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh-token")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        return Result.success(authService.refreshToken(token));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@CurrentUser Long userId) {
        authService.logout(userId);
        return Result.success();
    }
}
