package com.recharge.service.user;

import com.recharge.model.dto.response.UserInfoResponse;
import com.recharge.model.entity.User;

import java.math.BigDecimal;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    User getById(Long userId);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息响应
     */
    UserInfoResponse getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId   用户ID
     * @param nickname 昵称
     * @param avatar   头像
     */
    void updateUserInfo(Long userId, String nickname, String avatar);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 设置/修改支付密码
     *
     * @param userId      用户ID
     * @param payPassword 支付密码
     */
    void setPayPassword(Long userId, String payPassword);

    /**
     * 验证支付密码
     *
     * @param userId      用户ID
     * @param payPassword 支付密码
     * @return 是否正确
     */
    boolean verifyPayPassword(Long userId, String payPassword);

    /**
     * 增加用户余额
     *
     * @param userId     用户ID
     * @param amount     金额
     * @param relationId 关联业务ID
     * @param remark     备注
     */
    void addBalance(Long userId, BigDecimal amount, Long relationId, String remark);

    /**
     * 扣减用户余额
     *
     * @param userId     用户ID
     * @param amount     金额
     * @param relationId 关联业务ID
     * @param remark     备注
     */
    void deductBalance(Long userId, BigDecimal amount, Long relationId, String remark);
}
