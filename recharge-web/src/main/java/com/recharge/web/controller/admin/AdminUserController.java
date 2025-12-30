package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.UserMapper;
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
 * 管理后台 - 用户管理控制器
 */
@Tag(name = "管理后台-用户管理")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Operation(summary = "用户列表")
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "mobile", required = false) String mobile,
            @RequestParam(name = "status", required = false) Integer status) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(mobile), User::getMobile, mobile);
        wrapper.eq(status != null, User::getStatus, status);
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // Convert to frontend format
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
     * Convert user to frontend format
     */
    private Map<String, Object> convertToFrontendFormat(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("mobile", user.getMobile());
        map.put("nickname", user.getNickname());
        map.put("avatar", user.getHead()); // head -> avatar
        map.put("head", user.getHead());
        map.put("inviteCode", user.getInviteCode());
        map.put("price", user.getPrice());
        map.put("incomePrice", user.getIncomePrice());
        map.put("cashPrice", user.getCashPrice());
        map.put("level", user.getLevel());
        map.put("invitees", user.getInvitees());
        map.put("teamCount", user.getInvitees()); // Alias for frontend
        map.put("status", user.getStatus());
        map.put("createTime", user.getCreateTime() != null ? user.getCreateTime().format(FORMATTER) : null);
        map.put("updateTime", user.getUpdateTime() != null ? user.getUpdateTime().format(FORMATTER) : null);
        return map;
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{userId}")
    public Result<Map<String, Object>> detail(@PathVariable("userId") Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(convertToFrontendFormat(user));
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/{userId}/status")
    public Result<Void> updateStatus(@PathVariable("userId") Long userId,
                                     @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success();
    }

    @Operation(summary = "调整用户余额")
    @PutMapping("/{userId}/balance")
    public Result<Void> adjustBalance(@PathVariable("userId") Long userId,
                                      @RequestBody Map<String, Object> body) {
        String type = (String) body.get("type"); // add 或 deduct
        Number amountNum = (Number) body.get("amount");
        java.math.BigDecimal amount = new java.math.BigDecimal(amountNum.toString());

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        if ("add".equals(type)) {
            user.setPrice(user.getPrice().add(amount));
        } else if ("deduct".equals(type)) {
            if (user.getPrice().compareTo(amount) < 0) {
                return Result.fail("余额不足");
            }
            user.setPrice(user.getPrice().subtract(amount));
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success();
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{userId}/reset-password")
    public Result<Void> resetPassword(@PathVariable("userId") Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 重置为 123456 的 MD5
        user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success();
    }
}
