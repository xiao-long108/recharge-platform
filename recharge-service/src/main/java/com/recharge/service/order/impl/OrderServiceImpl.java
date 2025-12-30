package com.recharge.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.enums.ResultCodeEnum;
import com.recharge.common.exception.BusinessException;
import com.recharge.common.utils.OrderNoGenerator;
import com.recharge.mapper.OrderMapper;
import com.recharge.mapper.ProductMapper;
import com.recharge.model.dto.request.PayOrderRequest;
import com.recharge.model.dto.request.RechargeRequest;
import com.recharge.model.entity.Order;
import com.recharge.model.entity.Product;
import com.recharge.service.order.OrderService;
import com.recharge.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单服务实现 - 适配原数据库 cloud_times_api_orders
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createRechargeOrder(Long userId, RechargeRequest request) {
        // 1. 获取产品信息
        Product product = productMapper.selectById(request.getProductId());
        // 适配老表: disable=1表示启用
        if (product == null || product.getDisable() != 1) {
            throw new BusinessException("产品不存在或已下架");
        }

        // 2. 创建订单
        Order order = new Order();
        order.setOrderNo(OrderNoGenerator.generate());
        order.setUserId(userId);
        order.setGoodsId(product.getId());
        order.setStoreId(request.getStoreId());
        order.setMobile(request.getMobile());
        // 适配老表: realPrice是实际售价 (Integer -> BigDecimal)
        BigDecimal salePrice = product.getRealPrice() != null ?
                BigDecimal.valueOf(product.getRealPrice()) : BigDecimal.ZERO;
        order.setPrice(salePrice);                                  // 售价
        order.setPayAmount(salePrice);                              // 实付金额
        order.setStatus(0);                                         // 订单状态: 0=待处理
        order.setPayStatus(0);                                      // 支付状态: 0=未付款
        order.setType(request.getStoreId() != null ? 0 : 1);       // 0=店铺充值, 1=首页充值
        order.setCreateTime(LocalDateTime.now());

        orderMapper.insert(order);

        log.info("创建充值订单成功: orderNo={}, userId={}, mobile={}", order.getOrderNo(), userId, request.getMobile());

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long userId, Long orderId, PayOrderRequest request) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.ORDER_NOT_EXIST);
        }

        // 2. 检查订单状态 - 原数据库 pay_status: 0=未付款
        if (order.getPayStatus() != 0) {
            throw new BusinessException(ResultCodeEnum.ORDER_STATUS_ERROR);
        }

        // 3. 余额支付 (payType: 1=余额支付)
        if (request.getPayType() != null && request.getPayType() == 1) {
            // 验证支付密码
            if (!userService.verifyPayPassword(userId, request.getPayPassword())) {
                throw new BusinessException(ResultCodeEnum.USER_PAY_PASSWORD_ERROR);
            }

            // 扣减用户余额
            userService.deductBalance(userId, order.getPayAmount(), order.getId(), "订单支付: " + order.getOrderNo());

            // 更新订单状态
            order.setPayMethod("balance");
            order.setPayStatus(1);          // 1=已付款
            order.setStatus(1);             // 1=处理中
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);

            log.info("订单支付成功: orderNo={}, payType=balance", order.getOrderNo());

            // TODO: 触发充值（异步调用第三方充值接口）
        } else {
            // 第三方支付
            order.setPayMethod("third_party");
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);

            // TODO: 调用第三方支付接口，返回支付参数
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.ORDER_NOT_EXIST);
        }

        // 原数据库 pay_status: 0=未付款 才能取消
        if (order.getPayStatus() != 0) {
            throw new BusinessException(ResultCodeEnum.ORDER_STATUS_ERROR);
        }

        order.setStatus(-1);  // -1=已取消
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单已取消: orderNo={}", order.getOrderNo());
    }

    @Override
    public Order getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.ORDER_NOT_EXIST);
        }
        return order;
    }

    @Override
    public Page<Order> pageOrders(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);

        return orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRechargeCallback(String orderNo, boolean success, String message) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("充值回调订单不存在: orderNo={}", orderNo);
            return;
        }

        if (success) {
            order.setStatus(2);  // 2=充值成功
            order.setEndTime(LocalDateTime.now());
            log.info("充值成功: orderNo={}", orderNo);
        } else {
            order.setStatus(-2);  // -2=充值失败

            // 退款
            userService.addBalance(order.getUserId(), order.getPayAmount(), order.getId(), "充值失败退款: " + orderNo);
            log.info("充值失败，已退款: orderNo={}", orderNo);
        }

        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }
}
