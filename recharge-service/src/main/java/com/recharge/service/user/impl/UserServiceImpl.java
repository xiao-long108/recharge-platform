package com.recharge.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.common.enums.ResultCodeEnum;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.AccountFlowMapper;
import com.recharge.mapper.StoreMapper;
import com.recharge.mapper.UserMapper;
import com.recharge.model.dto.response.UserInfoResponse;
import com.recharge.model.entity.AccountFlow;
import com.recharge.model.entity.Store;
import com.recharge.model.entity.User;
import com.recharge.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户服务实现 - 适配原数据库 cloud_times_api_user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StoreMapper storeMapper;
    private final AccountFlowMapper accountFlowMapper;

    @Override
    public User getById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        }
        return user;
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = getById(userId);

        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setPhone(user.getMobile());                    // mobile -> phone
        response.setNickname(user.getNickname());
        response.setAvatar(user.getHead());                     // head -> avatar
        response.setBalance(user.getPrice());                   // price -> balance
        response.setCommissionBalance(user.getIncomePrice());   // income_price -> commissionBalance
        response.setAuthStatus(user.getStatus());               // status
        response.setAuthStatusDesc(getAuthStatusDesc(user.getStatus()));
        response.setInviteCode(user.getInviteCode());
        response.setLevel(user.getLevel());
        response.setHasPayPassword(StringUtils.hasText(user.getPayPassword()));

        // 查询店铺信息
        LambdaQueryWrapper<Store> storeWrapper = new LambdaQueryWrapper<>();
        storeWrapper.eq(Store::getUserId, userId);
        Store store = storeMapper.selectOne(storeWrapper);
        response.setHasStore(store != null);
        response.setStoreId(store != null ? store.getId() : null);

        return response;
    }

    @Override
    public void updateUserInfo(Long userId, String nickname, String avatar) {
        User user = getById(userId);
        if (StringUtils.hasText(nickname)) {
            user.setNickname(nickname);
        }
        if (StringUtils.hasText(avatar)) {
            user.setHead(avatar);  // head 字段
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("更新用户信息: userId={}", userId);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);

        // 验证旧密码 (MD5)
        if (!verifyMd5Password(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCodeEnum.USER_PASSWORD_ERROR);
        }

        // 更新密码 (MD5)
        user.setPassword(md5(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("修改密码成功: userId={}", userId);
    }

    @Override
    public void setPayPassword(Long userId, String payPassword) {
        User user = getById(userId);
        user.setPayPassword(md5(payPassword));  // 使用MD5
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("设置支付密码成功: userId={}", userId);
    }

    @Override
    public boolean verifyPayPassword(Long userId, String payPassword) {
        User user = getById(userId);
        if (!StringUtils.hasText(user.getPayPassword())) {
            throw new BusinessException(ResultCodeEnum.USER_PAY_PASSWORD_NOT_SET);
        }
        return verifyMd5Password(payPassword, user.getPayPassword());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBalance(Long userId, BigDecimal amount, Long relationId, String remark) {
        User user = getById(userId);
        BigDecimal balanceBefore = user.getPrice();

        // 增加余额 (price字段)
        user.setPrice(user.getPrice().add(amount));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 记录流水
        createAccountFlow(userId, 2, amount, balanceBefore, balanceBefore.add(amount), relationId, remark);

        log.info("增加用户余额: userId={}, amount={}", userId, amount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductBalance(Long userId, BigDecimal amount, Long relationId, String remark) {
        User user = getById(userId);

        // 检查余额 (price字段)
        if (user.getPrice().compareTo(amount) < 0) {
            throw new BusinessException(ResultCodeEnum.USER_BALANCE_INSUFFICIENT);
        }

        BigDecimal balanceBefore = user.getPrice();

        // 扣减余额
        user.setPrice(user.getPrice().subtract(amount));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 记录流水
        createAccountFlow(userId, 3, amount.negate(), balanceBefore, balanceBefore.subtract(amount), relationId, remark);

        log.info("扣减用户余额: userId={}, amount={}", userId, amount);
    }

    /**
     * 创建资金流水记录
     */
    private void createAccountFlow(Long userId, Integer dataType, BigDecimal amount,
                                   BigDecimal before, BigDecimal after, Long relationId, String remark) {
        AccountFlow flow = new AccountFlow();
        flow.setUserId(userId);
        flow.setDataType(dataType);  // 2:充值, 3:扣除
        flow.setPrice(amount.abs());
        flow.setBeforeAmount(before);
        flow.setAfterAmount(after);
        flow.setRelationId(relationId);
        flow.setRemarks(remark);
        flow.setStatus(1);  // 成功
        flow.setCreateTime(LocalDateTime.now());
        accountFlowMapper.insert(flow);
    }

    /**
     * 验证MD5密码
     */
    private boolean verifyMd5Password(String rawPassword, String encodedPassword) {
        return md5(rawPassword).equals(encodedPassword);
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

    private String getAuthStatusDesc(Integer status) {
        return switch (status) {
            case 0 -> "未实名";
            case 1 -> "已认证";
            case 2 -> "已冻结";
            case 3 -> "审核中";
            case 4 -> "审核不通过";
            default -> "未知";
        };
    }
}
