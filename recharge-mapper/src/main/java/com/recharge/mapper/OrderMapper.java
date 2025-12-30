package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单号查询订单
     */
    default Order selectByOrderNo(String orderNo) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
    }

    /**
     * 统计今日充值金额 (status=2 成功的订单)
     */
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM cloud_times_api_orders WHERE status = 2 AND DATE(create_time) = CURDATE()")
    BigDecimal sumTodayAmount();

    /**
     * 统计本月充值金额 (status=2 成功的订单)
     */
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM cloud_times_api_orders WHERE status = 2 AND YEAR(create_time) = YEAR(CURDATE()) AND MONTH(create_time) = MONTH(CURDATE())")
    BigDecimal sumMonthAmount();

    /**
     * 统计成功订单总金额
     */
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM cloud_times_api_orders WHERE status = 2")
    BigDecimal sumTotalAmount();

    /**
     * 统计成功订单数
     */
    @Select("SELECT COUNT(*) FROM cloud_times_api_orders WHERE status = 2")
    Long countSuccessOrders();
}
