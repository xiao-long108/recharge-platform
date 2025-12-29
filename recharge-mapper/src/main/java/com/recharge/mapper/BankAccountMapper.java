package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.BankAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 银行账户Mapper - 适配原表 cloud_times_api_account
 */
@Mapper
public interface BankAccountMapper extends BaseMapper<BankAccount> {

    /**
     * 查询用户的银行账户列表
     */
    default List<BankAccount> selectByUserId(Long userId) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BankAccount>()
                .eq(BankAccount::getUserId, userId)
                .orderByDesc(BankAccount::getIsDefault)
                .orderByDesc(BankAccount::getCreateTime));
    }

    /**
     * 查询用户的默认银行账户
     */
    default BankAccount selectDefaultByUserId(Long userId) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BankAccount>()
                .eq(BankAccount::getUserId, userId)
                .eq(BankAccount::getIsDefault, 1));
    }

    /**
     * 取消用户所有默认账户
     */
    @Update("UPDATE cloud_times_api_account SET is_default = 2, update_time = NOW() WHERE user_id = #{userId}")
    int cancelDefault(@Param("userId") Long userId);
}
