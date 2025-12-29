package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.common.result.Result;
import com.recharge.mapper.OrderMapper;
import com.recharge.mapper.UserMapper;
import com.recharge.mapper.AccountFlowMapper;
import com.recharge.mapper.ProductMapper;
import com.recharge.model.entity.Order;
import com.recharge.model.entity.User;
import com.recharge.model.entity.AccountFlow;
import com.recharge.model.entity.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理后台 - 数据统计控制器
 */
@Tag(name = "管理后台-数据统计")
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final AccountFlowMapper accountFlowMapper;
    private final ProductMapper productMapper;

    @Operation(summary = "首页统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> stats = new HashMap<>();

        // 用户统计
        Long totalUsers = userMapper.selectCount(null);
        stats.put("totalUsers", totalUsers);

        // 今日新增用户
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayUsers = userMapper.selectCount(
            new LambdaQueryWrapper<User>().ge(User::getCreateTime, todayStart)
        );
        stats.put("todayUsers", todayUsers);

        // 订单统计
        Long totalOrders = orderMapper.selectCount(null);
        stats.put("totalOrders", totalOrders);

        // 今日订单
        Long todayOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().ge(Order::getCreateTime, todayStart)
        );
        stats.put("todayOrders", todayOrders);

        // 成功订单数 (status=3 充值成功)
        Long successOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 3)
        );
        stats.put("successOrders", successOrders);

        // 待处理订单 (status in 0,1,2: 待支付、支付中、充值中)
        Long pendingOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().in(Order::getStatus, 0, 1, 2)
        );
        stats.put("pendingOrders", pendingOrders);

        // 待审核提现
        Long pendingWithdraws = accountFlowMapper.selectCount(
            new LambdaQueryWrapper<AccountFlow>()
                .eq(AccountFlow::getDataType, 1)
                .eq(AccountFlow::getStatus, 3)
        );
        stats.put("pendingWithdraws", pendingWithdraws);

        // 总交易金额（已支付订单）
        // 简化处理，实际应该用sum聚合
        stats.put("totalAmount", BigDecimal.ZERO);

        return Result.success(stats);
    }

    @Operation(summary = "用户统计")
    @GetMapping("/users")
    public Result<Map<String, Object>> userStats() {
        Map<String, Object> stats = new HashMap<>();

        Long total = userMapper.selectCount(null);
        Long normal = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getStatus, 1)
        );
        Long frozen = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getStatus, 2)
        );

        stats.put("total", total);
        stats.put("normal", normal);
        stats.put("frozen", frozen);

        return Result.success(stats);
    }

    @Operation(summary = "订单统计")
    @GetMapping("/orders")
    public Result<Map<String, Object>> orderStats() {
        Map<String, Object> stats = new HashMap<>();

        Long total = orderMapper.selectCount(null);
        // 待支付 (status=0)
        Long pending = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 0)
        );
        // 处理中 (status in 1,2: 支付中、充值中)
        Long processing = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().in(Order::getStatus, 1, 2)
        );
        // 成功 (status=3 充值成功)
        Long success = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 3)
        );
        // 失败 (status=4 充值失败)
        Long failed = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 4)
        );

        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("processing", processing);
        stats.put("success", success);
        stats.put("failed", failed);

        return Result.success(stats);
    }

    @Operation(summary = "综合统计数据")
    @GetMapping("")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户统计
        Long totalUsers = userMapper.selectCount(null);
        stats.put("totalUsers", totalUsers);

        // 今日新增用户
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayUsers = userMapper.selectCount(
            new LambdaQueryWrapper<User>().ge(User::getCreateTime, todayStart)
        );
        stats.put("todayUsers", todayUsers);

        // 订单统计
        Long totalOrders = orderMapper.selectCount(null);
        stats.put("totalOrders", totalOrders);

        // 今日订单
        Long todayOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().ge(Order::getCreateTime, todayStart)
        );
        stats.put("todayOrders", todayOrders);

        // 成功订单数 (status=3 充值成功)
        Long successOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 3)
        );
        stats.put("successOrders", successOrders);

        // 待处理订单 (status in 0,1,2: 待支付、支付中、充值中)
        Long pendingOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().in(Order::getStatus, 0, 1, 2)
        );
        stats.put("pendingOrders", pendingOrders);

        // 产品统计
        Long totalProducts = productMapper.selectCount(null);
        stats.put("totalProducts", totalProducts);

        // 待审核提现 (使用AccountFlow表，data_type=1表示提现，status=3表示等待审核)
        Long pendingWithdraws = accountFlowMapper.selectCount(
            new LambdaQueryWrapper<AccountFlow>()
                .eq(AccountFlow::getDataType, 1)
                .eq(AccountFlow::getStatus, 3)
        );
        stats.put("pendingWithdraws", pendingWithdraws);

        return Result.success(stats);
    }

    @Operation(summary = "产品统计")
    @GetMapping("/products")
    public Result<Map<String, Object>> productStats() {
        Map<String, Object> stats = new HashMap<>();

        // 产品总数
        Long total = productMapper.selectCount(null);
        stats.put("total", total);
        stats.put("totalProducts", total);  // 兼容Products.vue页面

        // 启用的产品数 (disable=1 表示启用)
        Long enabled = productMapper.selectCount(
            new LambdaQueryWrapper<Product>().eq(Product::getDisable, 1)
        );
        stats.put("enabled", enabled);
        stats.put("enabledProducts", enabled);  // 兼容Products.vue页面

        // 禁用的产品数 (disable=0 表示禁用)
        Long disabled = productMapper.selectCount(
            new LambdaQueryWrapper<Product>().eq(Product::getDisable, 0)
        );
        stats.put("disabled", disabled);
        stats.put("disabledProducts", disabled);  // 兼容Products.vue页面

        return Result.success(stats);
    }

    @Operation(summary = "提现统计")
    @GetMapping("/withdraws")
    public Result<Map<String, Object>> withdrawStats() {
        Map<String, Object> stats = new HashMap<>();

        // 使用AccountFlow表统计提现，data_type=1表示提现申请
        // 状态: 1=成功, 2=失败, 3=等待审核

        // 提现总数
        Long total = accountFlowMapper.selectCount(
            new LambdaQueryWrapper<AccountFlow>().eq(AccountFlow::getDataType, 1)
        );
        stats.put("total", total);

        // 待审核 (status=3)
        Long pending = accountFlowMapper.selectCount(
            new LambdaQueryWrapper<AccountFlow>()
                .eq(AccountFlow::getDataType, 1)
                .eq(AccountFlow::getStatus, 3)
        );
        stats.put("pending", pending);

        // 处理中 (假设没有处理中状态，设为0)
        stats.put("processing", 0L);

        // 成功 (status=1)
        Long success = accountFlowMapper.selectCount(
            new LambdaQueryWrapper<AccountFlow>()
                .eq(AccountFlow::getDataType, 1)
                .eq(AccountFlow::getStatus, 1)
        );
        stats.put("success", success);

        // 失败 (status=2)
        Long failed = accountFlowMapper.selectCount(
            new LambdaQueryWrapper<AccountFlow>()
                .eq(AccountFlow::getDataType, 1)
                .eq(AccountFlow::getStatus, 2)
        );
        stats.put("failed", failed);

        // 已取消 (假设没有取消状态，设为0)
        stats.put("cancelled", 0L);

        return Result.success(stats);
    }
}
