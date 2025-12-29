package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.YuebaoAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 余额宝账户Mapper
 */
@Mapper
public interface YuebaoAccountMapper extends BaseMapper<YuebaoAccount> {

    /**
     * 根据用户ID查询余额宝账户
     */
    default YuebaoAccount selectByUserId(Long userId) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<YuebaoAccount>()
                .eq(YuebaoAccount::getUserId, userId));
    }

    /**
     * 增加余额宝余额
     */
    @Update("UPDATE t_yuebao_account SET balance = balance + #{amount}, updated_time = NOW() WHERE user_id = #{userId}")
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 扣减余额宝余额
     */
    @Update("UPDATE t_yuebao_account SET balance = balance - #{amount}, updated_time = NOW() WHERE user_id = #{userId} AND balance >= #{amount}")
    int deductBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 分批查询账户（用于计算收益）
     */
    @Select("SELECT * FROM t_yuebao_account WHERE balance > 0 AND status = 1 LIMIT #{offset}, #{limit}")
    List<YuebaoAccount> selectBatch(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 更新昨日收益和累计收益
     */
    @Update("UPDATE t_yuebao_account SET balance = balance + #{income}, yesterday_income = #{income}, total_income = total_income + #{income}, updated_time = NOW() WHERE id = #{id}")
    int updateIncome(@Param("id") Long id, @Param("income") BigDecimal income);
}
