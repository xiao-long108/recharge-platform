package com.recharge.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.PageResult;
import com.recharge.common.result.Result;
import com.recharge.model.dto.response.CouponListResponse;
import com.recharge.model.dto.response.CouponResponse;
import com.recharge.service.coupon.CouponService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券控制器
 */
@Tag(name = "优惠券管理", description = "优惠券领取、查询、使用")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "获取可领取优惠券列表")
    @GetMapping("/available")
    public Result<List<CouponListResponse>> getAvailableCoupons(@CurrentUser Long userId) {
        return Result.success(couponService.getAvailableCoupons(userId));
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/{couponId}/receive")
    public Result<Void> receiveCoupon(@CurrentUser Long userId,
                                       @PathVariable Long couponId) {
        couponService.receiveCoupon(userId, couponId);
        return Result.success();
    }

    @Operation(summary = "获取我的优惠券列表")
    @GetMapping("/my")
    public Result<PageResult<CouponResponse>> getMyCoupons(
            @CurrentUser Long userId,
            @Parameter(description = "状态: 0-未使用 1-已使用 2-已过期") @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<CouponResponse> result = couponService.getMyCoupons(userId, status, page, size);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Operation(summary = "获取可用优惠券（订单结算）")
    @GetMapping("/usable")
    public Result<List<CouponResponse>> getUsableCoupons(
            @CurrentUser Long userId,
            @Parameter(description = "订单金额") @RequestParam BigDecimal orderAmount) {
        return Result.success(couponService.getUsableCoupons(userId, orderAmount));
    }

    @Operation(summary = "统计可用优惠券数量")
    @GetMapping("/count")
    public Result<Integer> countAvailable(@CurrentUser Long userId) {
        return Result.success(couponService.countAvailable(userId));
    }
}
