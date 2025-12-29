package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.CommissionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 佣金记录Mapper
 */
@Mapper
public interface CommissionRecordMapper extends BaseMapper<CommissionRecord> {

    /**
     * 根据订单ID查询佣金记录
     */
    default List<CommissionRecord> selectByOrderId(Long orderId) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CommissionRecord>()
                .eq(CommissionRecord::getOrderId, orderId));
    }

    /**
     * 根据订单ID更新状态
     */
    @Update("UPDATE t_commission_record SET status = #{status}, updated_time = NOW() WHERE order_id = #{orderId}")
    int updateStatusByOrderId(@Param("orderId") Long orderId, @Param("status") Integer status);
}
