package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.UserDistribution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户分销等级Mapper
 */
@Mapper
public interface UserDistributionMapper extends BaseMapper<UserDistribution> {

    /**
     * 根据用户ID查询分销等级
     */
    @Select("SELECT * FROM t_user_distribution WHERE user_id = #{userId} AND status = 1 AND deleted = 0")
    UserDistribution selectByUserId(@Param("userId") Long userId);
}
