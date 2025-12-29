package com.recharge.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.PageResult;
import com.recharge.common.result.Result;
import com.recharge.model.dto.request.PayOrderRequest;
import com.recharge.model.dto.request.RechargeRequest;
import com.recharge.model.entity.Order;
import com.recharge.service.order.OrderService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Tag(name = "订单管理", description = "充值订单相关")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建充值订单")
    @PostMapping("/recharge")
    public Result<Order> createRechargeOrder(@CurrentUser Long userId,
                                              @RequestBody @Validated RechargeRequest request) {
        return Result.success(orderService.createRechargeOrder(userId, request));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{orderId}/pay")
    public Result<Void> payOrder(@CurrentUser Long userId,
                                  @PathVariable Long orderId,
                                  @RequestBody @Validated PayOrderRequest request) {
        orderService.payOrder(userId, orderId, request);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@CurrentUser Long userId,
                                     @PathVariable Long orderId) {
        orderService.cancelOrder(userId, orderId);
        return Result.success();
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@CurrentUser Long userId,
                                         @PathVariable Long orderId) {
        return Result.success(orderService.getOrderDetail(userId, orderId));
    }

    @Operation(summary = "订单列表")
    @GetMapping
    public Result<PageResult<Order>> pageOrders(@CurrentUser Long userId,
                                                 @RequestParam(required = false) Integer status,
                                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Order> page = orderService.pageOrders(userId, status, pageNum, pageSize);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }
}
