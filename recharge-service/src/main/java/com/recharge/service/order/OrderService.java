package com.recharge.service.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.model.dto.request.PayOrderRequest;
import com.recharge.model.dto.request.RechargeRequest;
import com.recharge.model.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建充值订单
     *
     * @param userId  用户ID
     * @param request 充值请求
     * @return 订单
     */
    Order createRechargeOrder(Long userId, RechargeRequest request);

    /**
     * 支付订单
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param request 支付请求
     */
    void payOrder(Long userId, Long orderId, PayOrderRequest request);

    /**
     * 取消订单
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 获取订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单
     */
    Order getOrderDetail(Long userId, Long orderId);

    /**
     * 分页查询订单
     *
     * @param userId   用户ID
     * @param status   状态（可选）
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<Order> pageOrders(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 处理充值回调
     *
     * @param orderNo 订单号
     * @param success 是否成功
     * @param message 消息
     */
    void handleRechargeCallback(String orderNo, boolean success, String message);
}
