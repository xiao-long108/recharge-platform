package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.OrderMapper;
import com.recharge.mapper.UserMapper;
import com.recharge.model.entity.Order;
import com.recharge.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 订单管理控制器
 */
@Tag(name = "管理后台-订单管理")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Operation(summary = "订单列表")
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orderNo", required = false) String orderNo,
            @RequestParam(name = "mobile", required = false) String mobile,
            @RequestParam(name = "status", required = false) Integer status) {

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(orderNo), Order::getOrderNo, orderNo);
        wrapper.like(StringUtils.hasText(mobile), Order::getMobile, mobile);
        wrapper.eq(status != null, Order::getStatus, status);
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> page = orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // Convert to frontend format with user info
        List<Map<String, Object>> records = page.getRecords().stream()
                .map(this::convertToFrontendFormat)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());

        return Result.success(result);
    }

    /**
     * Convert order to frontend format with user info
     */
    private Map<String, Object> convertToFrontendFormat(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("userId", order.getUserId());
        map.put("mobile", order.getMobile());
        map.put("price", order.getPrice());
        map.put("payAmount", order.getPayAmount());
        map.put("payMethod", order.getPayMethod());
        map.put("payChannel", order.getPayMethod()); // Alias for frontend
        map.put("payStatus", order.getPayStatus());
        map.put("status", order.getStatus());
        map.put("profit", order.getProfit());
        map.put("createTime", order.getCreateTime() != null ? order.getCreateTime().format(FORMATTER) : null);
        map.put("updateTime", order.getUpdateTime() != null ? order.getUpdateTime().format(FORMATTER) : null);
        map.put("endTime", order.getEndTime() != null ? order.getEndTime().format(FORMATTER) : null);
        map.put("payTime", order.getEndTime() != null ? order.getEndTime().format(FORMATTER) : null); // Alias

        // Add user info
        if (order.getUserId() != null) {
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                map.put("userNickname", user.getNickname());
                map.put("userAvatar", user.getHead());
            } else {
                map.put("userNickname", "用户" + order.getUserId());
                map.put("userAvatar", null);
            }
        } else {
            map.put("userNickname", "未知用户");
            map.put("userAvatar", null);
        }

        return map;
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderId}")
    public Result<Map<String, Object>> detail(@PathVariable("orderId") Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(convertToFrontendFormat(order));
    }

    @Operation(summary = "修改订单状态")
    @PutMapping("/{orderId}/status")
    public Result<Void> updateStatus(@PathVariable("orderId") Long orderId,
                                     @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());

        // 如果标记为成功，记录完成时间
        if (status == 2) {
            order.setEndTime(LocalDateTime.now());
        }

        orderMapper.updateById(order);
        return Result.success();
    }

    @Operation(summary = "手动退款")
    @PostMapping("/{orderId}/refund")
    public Result<Void> refund(@PathVariable("orderId") Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }

        if (order.getPayStatus() != 1) {
            return Result.fail("订单未支付，无法退款");
        }

        // 退款到用户余额
        var user = userMapper.selectById(order.getUserId());
        if (user != null && order.getPayAmount() != null) {
            user.setPrice(user.getPrice().add(order.getPayAmount()));
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }

        // 更新订单状态
        order.setStatus(-3); // -3 = 已退款
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        return Result.success();
    }
}
