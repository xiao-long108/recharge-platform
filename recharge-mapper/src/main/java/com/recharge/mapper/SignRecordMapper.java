package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.SignRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 签到记录Mapper
 */
@Mapper
public interface SignRecordMapper extends BaseMapper<SignRecord> {

    /**
     * 查询用户今日签到记录
     */
    @Select("SELECT * FROM t_sign_record WHERE user_id = #{userId} AND sign_date = #{date} AND deleted = 0")
    SignRecord selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * 统计用户累计签到天数
     */
    @Select("SELECT COUNT(*) FROM t_sign_record WHERE user_id = #{userId} AND deleted = 0")
    Integer countTotalDays(@Param("userId") Long userId);

    /**
     * 查询用户最近一次签到记录
     */
    @Select("SELECT * FROM t_sign_record WHERE user_id = #{userId} AND deleted = 0 ORDER BY sign_date DESC LIMIT 1")
    SignRecord selectLastRecord(@Param("userId") Long userId);
}
