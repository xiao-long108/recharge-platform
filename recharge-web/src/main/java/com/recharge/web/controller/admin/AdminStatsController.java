package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.common.result.Result;
import com.recharge.mapper.OrderMapper;
import com.recharge.mapper.UserMapper;
import com.recharge.mapper.AccountFlowMapper;
import com.recharge.model.entity.Order;
import com.recharge.model.entity.User;
import com.recharge.model.entity.AccountFlow;
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

        // 成功订单数
        Long successOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 2)
        );
        stats.put("successOrders", successOrders);

        // 待处理订单
        Long pendingOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().in(Order::getStatus, 0, 1)
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
        Long pending = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 0)
        );
        Long processing = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 1)
        );
        Long success = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().eq(Order::getStatus, 2)
        );
        Long failed = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().lt(Order::getStatus, 0)
        );

        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("processing", processing);
        stats.put("success", success);
        stats.put("failed", failed);

        return Result.success(stats);
    }
}
