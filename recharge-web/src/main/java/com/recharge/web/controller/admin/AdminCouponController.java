package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.CouponMapper;
import com.recharge.model.entity.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 优惠券管理控制器
 * 对应原表: cloud_times_api_voucher
 */
@Tag(name = "管理后台-优惠券管理")
@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponMapper couponMapper;

    @Operation(summary = "优惠券列表")
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "status", required = false) Integer status) {

        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Coupon::getName, keyword);
        }
        if (type != null) {
            wrapper.eq(Coupon::getType, type);
        }

        wrapper.orderByDesc(Coupon::getId);

        Page<Coupon> page = couponMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 转换为前端格式
        List<Map<String, Object>> records = page.getRecords().stream()
                .map(this::convertToFrontendFormat)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        result.put("pages", page.getPages());

        return Result.success(result);
    }

    @Operation(summary = "优惠券统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        // 总优惠券数
        Long total = couponMapper.selectCount(null);
        stats.put("total", total);
        stats.put("totalCoupons", total);

        // 已领取数量（老表没有领取记录，暂时用总数模拟）
        stats.put("claimed", 0L);

        // 已使用数量（老表没有使用记录）
        stats.put("used", 0L);

        // 优惠总额（老表没有统计，返回0）
        stats.put("totalDiscount", 0);

        // 已过期优惠券数 (endtime < 当前日期)
        Long expired = couponMapper.selectCount(
            new LambdaQueryWrapper<Coupon>().lt(Coupon::getEndtime, LocalDate.now())
        );
        stats.put("expiredCoupons", expired);

        return Result.success(stats);
    }

    @Operation(summary = "优惠券详情")
    @GetMapping("/{couponId}")
    public Result<Map<String, Object>> detail(@PathVariable("couponId") Integer couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return Result.fail("优惠券不存在");
        }
        return Result.success(convertToFrontendFormat(coupon));
    }

    @Operation(summary = "添加优惠券")
    @PostMapping
    public Result<Void> add(@RequestBody CouponRequest request) {
        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        // 使用 getTypeValue() 支持前端发送 type 或 couponType
        Integer typeValue = request.getTypeValue();
        coupon.setType(typeValue != null ? typeValue : 1);
        coupon.setDescription(request.getDescription());
        coupon.setAmount(request.getDiscountAmount() != null ? request.getDiscountAmount().intValue() : 0);
        coupon.setImage(request.getImage());
        coupon.setProductId(request.getProductId() != null ? request.getProductId() : 0);
        coupon.setMethod(request.getMethod() != null ? request.getMethod() : 1);
        coupon.setNum(request.getTotalCount() != null ? request.getTotalCount() : 1);

        if (request.getStartTime() != null) {
            coupon.setStarttime(request.getStartTime().toLocalDate());
        } else {
            coupon.setStarttime(LocalDate.now());
        }
        if (request.getEndTime() != null) {
            coupon.setEndtime(request.getEndTime().toLocalDate());
        } else {
            coupon.setEndtime(LocalDate.now().plusMonths(1));
        }
        coupon.setAddtime((int) (System.currentTimeMillis() / 1000));

        couponMapper.insert(coupon);
        return Result.success();
    }

    @Operation(summary = "修改优惠券")
    @PutMapping("/{couponId}")
    public Result<Void> update(@PathVariable("couponId") Integer couponId,
                               @RequestBody CouponRequest request) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return Result.fail("优惠券不存在");
        }

        if (request.getName() != null) {
            coupon.setName(request.getName());
        }
        // 使用 getTypeValue() 支持前端发送 type 或 couponType
        Integer typeValue = request.getTypeValue();
        if (typeValue != null) {
            coupon.setType(typeValue);
        }
        if (request.getDescription() != null) {
            coupon.setDescription(request.getDescription());
        }
        if (request.getDiscountAmount() != null) {
            coupon.setAmount(request.getDiscountAmount().intValue());
        }
        if (request.getImage() != null) {
            coupon.setImage(request.getImage());
        }
        if (request.getProductId() != null) {
            coupon.setProductId(request.getProductId());
        }
        if (request.getMethod() != null) {
            coupon.setMethod(request.getMethod());
        }
        if (request.getTotalCount() != null) {
            coupon.setNum(request.getTotalCount());
        }
        if (request.getStartTime() != null) {
            coupon.setStarttime(request.getStartTime().toLocalDate());
        }
        if (request.getEndTime() != null) {
            coupon.setEndtime(request.getEndTime().toLocalDate());
        }

        couponMapper.updateById(coupon);
        return Result.success();
    }

    @Operation(summary = "上架/下架优惠券")
    @PutMapping("/{couponId}/toggle")
    public Result<Void> toggle(@PathVariable("couponId") Integer couponId) {
        // 老表没有状态字段，忽略此操作
        return Result.success();
    }

    @Operation(summary = "删除优惠券")
    @DeleteMapping("/{couponId}")
    public Result<Void> delete(@PathVariable("couponId") Integer couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return Result.fail("优惠券不存在");
        }

        // 物理删除
        couponMapper.deleteById(couponId);
        return Result.success();
    }

    /**
     * 转换为前端格式
     */
    private Map<String, Object> convertToFrontendFormat(Coupon coupon) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", coupon.getId());
        map.put("name", coupon.getName());
        map.put("type", coupon.getType());  // 前端期望 type
        map.put("couponType", coupon.getType());  // 兼容
        map.put("discountAmount", coupon.getAmount());
        map.put("discountRate", null);
        map.put("minAmount", 0);
        map.put("maxDiscount", null);
        map.put("totalCount", coupon.getNum());
        map.put("claimedCount", 0);  // 已领取数（老表无此字段）
        map.put("usedCount", 0);
        map.put("perLimit", 1);  // 前端期望 perLimit
        map.put("perUserLimit", 1);  // 兼容
        map.put("validDays", null);
        map.put("startTime", coupon.getStarttime());
        map.put("endTime", coupon.getEndtime());
        map.put("useScope", coupon.getProductId() != null && coupon.getProductId() > 0 ? 1 : 0);
        map.put("scopeIds", coupon.getProductId() != null && coupon.getProductId() > 0 ? String.valueOf(coupon.getProductId()) : null);
        map.put("description", coupon.getDescription());
        map.put("image", coupon.getImage());
        map.put("method", coupon.getMethod());
        // 根据过期时间判断状态: 0=已停止, 1=进行中, 2=已结束
        int status = 1;
        if (coupon.getEndtime() != null && coupon.getEndtime().isBefore(LocalDate.now())) {
            status = 2;  // 已结束
        }
        map.put("status", status);
        map.put("createdTime", coupon.getCreatedTime());
        return map;
    }

    @Data
    public static class CouponRequest {
        private String name;
        private Integer couponType;
        private Integer type;  // 前端发送 type
        private BigDecimal discountAmount;
        private BigDecimal discountRate;
        private BigDecimal minAmount;
        private BigDecimal maxDiscount;
        private Integer totalCount;
        private Integer perUserLimit;
        private Integer perLimit;  // 前端发送 perLimit
        private Integer validDays;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer useScope;
        private String scopeIds;
        private String description;
        private String image;
        private Integer productId;
        private Integer method;
        private Integer status;

        // 获取类型，优先使用 type
        public Integer getTypeValue() {
            return type != null ? type : couponType;
        }

        // 获取每人限领，优先使用 perLimit
        public Integer getPerLimitValue() {
            return perLimit != null ? perLimit : perUserLimit;
        }
    }
}
