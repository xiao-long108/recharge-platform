package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 用户Mapper - 适配原表 cloud_times_api_user
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户
     */
    default User selectByMobile(String mobile) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getMobile, mobile));
    }

    /**
     * 根据邀请码查询用户
     */
    default User selectByInviteCode(String inviteCode) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getInviteCode, inviteCode));
    }

    /**
     * 增加用户余额 (price字段)
     */
    @Update("UPDATE cloud_times_api_user SET price = price + #{amount}, update_time = NOW() WHERE id = #{userId}")
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 扣减用户余额 (price字段)
     */
    @Update("UPDATE cloud_times_api_user SET price = price - #{amount}, update_time = NOW() WHERE id = #{userId} AND price >= #{amount}")
    int deductBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 增加佣金余额 (income_price字段)
     */
    @Update("UPDATE cloud_times_api_user SET income_price = income_price + #{amount}, update_time = NOW() WHERE id = #{userId}")
    int addCommissionBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 扣减佣金余额 (income_price字段)
     */
    @Update("UPDATE cloud_times_api_user SET income_price = income_price - #{amount}, update_time = NOW() WHERE id = #{userId} AND income_price >= #{amount}")
    int deductCommissionBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 统计所有用户总余额
     */
    @org.apache.ibatis.annotations.Select("SELECT COALESCE(SUM(price), 0) FROM cloud_times_api_user WHERE delete_time IS NULL")
    BigDecimal sumTotalBalance();

    /**
     * 统计所有用户总佣金
     */
    @org.apache.ibatis.annotations.Select("SELECT COALESCE(SUM(income_price), 0) FROM cloud_times_api_user WHERE delete_time IS NULL")
    BigDecimal sumTotalCommission();
}
