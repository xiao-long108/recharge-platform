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
import java.util.Map;

/**
 * 管理后台 - 用户管理控制器
 */
@Tag(name = "管理后台-用户管理")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;

    @Operation(summary = "用户列表")
    @GetMapping
    public Result<Page<User>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "mobile", required = false) String mobile,
            @RequestParam(name = "status", required = false) Integer status) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(mobile), User::getMobile, mobile);
        wrapper.eq(status != null, User::getStatus, status);
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{userId}")
    public Result<User> detail(@PathVariable Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null); // 隐藏密码
            user.setPayPassword(null);
        }
        return Result.success(user);
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/{userId}/status")
    public Result<Void> updateStatus(@PathVariable Long userId,
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
    public Result<Void> adjustBalance(@PathVariable Long userId,
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
    public Result<Void> resetPassword(@PathVariable Long userId) {
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
