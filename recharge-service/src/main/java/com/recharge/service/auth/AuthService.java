package com.recharge.service.auth;

import com.recharge.model.dto.request.LoginRequest;
import com.recharge.model.dto.request.RegisterRequest;
import com.recharge.model.dto.response.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     */
    void register(RegisterRequest request);

    /**
     * 刷新Token
     *
     * @param oldToken 旧Token
     * @return 新Token
     */
    String refreshToken(String oldToken);

    /**
     * 登出
     *
     * @param userId 用户ID
     */
    void logout(Long userId);
}
