package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.AccountFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户流水Mapper
 */
@Mapper
public interface AccountFlowMapper extends BaseMapper<AccountFlow> {

    /**
     * 按条件统计金额
     * @param dataType 数据类型 (1=提现)
     * @param status 状态 (1=成功, 2=失败, 3=待审核)
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @Select("<script>" +
            "SELECT COALESCE(SUM(price), 0) FROM cloud_times_api_fund_detail " +
            "WHERE data_type = #{dataType} AND status = #{status} AND delete_time IS NULL " +
            "<if test='startTime != null'>AND create_time &gt;= #{startTime}</if> " +
            "<if test='endTime != null'>AND create_time &lt;= #{endTime}</if> " +
            "</script>")
    BigDecimal sumAmountByCondition(@Param("dataType") Integer dataType,
                                    @Param("status") Integer status,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
}
