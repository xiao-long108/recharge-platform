package com.recharge.service.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.common.constant.RedisKeyConstant;
import com.recharge.common.enums.ResultCodeEnum;
import com.recharge.common.exception.BusinessException;
import com.recharge.common.utils.JwtUtils;
import com.recharge.mapper.UserMapper;
import com.recharge.model.dto.request.LoginRequest;
import com.recharge.model.dto.request.RegisterRequest;
import com.recharge.model.dto.response.LoginResponse;
import com.recharge.model.entity.User;
import com.recharge.service.auth.AuthService;
import com.recharge.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现 - 适配原数据库 cloud_times_api_user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final TeamService teamService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查询用户 (使用mobile字段)
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getMobile, request.getPhone());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        }

        // 2. 验证密码 (原数据库密码可能是MD5)
        if (!verifyPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCodeEnum.USER_PASSWORD_ERROR);
        }

        // 3. 检查账户状态 (原数据库: 1=正常, 2=冻结)
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCodeEnum.USER_ACCOUNT_DISABLED);
        }

        // 4. 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getMobile());

        // 5. Token存入Redis
        String redisKey = RedisKeyConstant.USER_TOKEN_PREFIX + user.getId();
        redisTemplate.opsForValue().set(redisKey, token, RedisKeyConstant.TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 6. 更新登录时间和Token
        user.setLoginTime((int) (System.currentTimeMillis() / 1000));
        user.setToken(token);
        userMapper.updateById(user);

        log.info("用户登录成功: userId={}, mobile={}", user.getId(), user.getMobile());

        // 7. 构建返回
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .phone(user.getMobile())
                .nickname(user.getNickname())
                .avatar(user.getHead())
                .hasPayPassword(StringUtils.hasText(user.getPayPassword()))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        // 1. 验证密码一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入密码不一致");
        }

        // 2. 验证手机号是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getMobile, request.getPhone());
        User existUser = userMapper.selectOne(wrapper);

        if (existUser != null) {
            throw new BusinessException(ResultCodeEnum.USER_PHONE_EXIST);
        }

        // 3. 创建用户 (适配原数据库字段)
        User user = new User();
        user.setMobile(request.getPhone());
        user.setPassword(encodePassword(request.getPassword()));
        user.setNickname("用户" + request.getPhone().substring(7));
        user.setPrice(BigDecimal.ZERO);           // 可用金额
        user.setIncomePrice(BigDecimal.ZERO);     // 总佣金
        user.setCashPrice(BigDecimal.ZERO);       // 可提余额
        user.setStatus(1);                         // 1=正常
        user.setInviteCode(generateInviteCode());
        user.setLevel(0);
        user.setInvitees(0);
        user.setCreateTime(LocalDateTime.now());

        // 4. 处理邀请码（上级关系）
        if (StringUtils.hasText(request.getInviteCode())) {
            LambdaQueryWrapper<User> parentWrapper = new LambdaQueryWrapper<>();
            parentWrapper.eq(User::getInviteCode, request.getInviteCode());
            User parentUser = userMapper.selectOne(parentWrapper);

            if (parentUser != null) {
                user.setParentUserId(parentUser.getId());
            }
        }

        userMapper.insert(user);

        // 5. 建立团队关系
        if (user.getParentUserId() != null) {
            teamService.createTeamRelation(user.getId(), user.getParentUserId());
        }

        log.info("用户注册成功: userId={}, mobile={}", user.getId(), user.getMobile());
    }

    @Override
    public String refreshToken(String oldToken) {
        if (!jwtUtils.validateToken(oldToken)) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }

        Long userId = jwtUtils.getUserIdFromToken(oldToken);
        String phone = jwtUtils.getPhoneFromToken(oldToken);

        // 生成新Token
        String newToken = jwtUtils.generateToken(userId, phone);

        // 更新Redis
        String redisKey = RedisKeyConstant.USER_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(redisKey, newToken, RedisKeyConstant.TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        return newToken;
    }

    @Override
    public void logout(Long userId) {
        String redisKey = RedisKeyConstant.USER_TOKEN_PREFIX + userId;
        redisTemplate.delete(redisKey);
        log.info("用户登出: userId={}", userId);
    }

    /**
     * 生成邀请码
     */
    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    /**
     * 验证密码 - 兼容原数据库的MD5密码
     */
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        // 先尝试BCrypt
        try {
            if (passwordEncoder.matches(rawPassword, encodedPassword)) {
                return true;
            }
        } catch (Exception ignored) {
        }

        // 再尝试MD5 (原数据库可能使用MD5)
        String md5Password = md5(rawPassword);
        return md5Password.equals(encodedPassword);
    }

    /**
     * 加密密码 - 使用MD5保持与原数据库兼容
     */
    private String encodePassword(String rawPassword) {
        return md5(rawPassword);
    }

    /**
     * MD5加密
     */
    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
