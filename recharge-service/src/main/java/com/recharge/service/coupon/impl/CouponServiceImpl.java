package com.recharge.service.coupon.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.CouponMapper;
import com.recharge.mapper.UserCouponMapper;
import com.recharge.model.dto.response.CouponListResponse;
import com.recharge.model.dto.response.CouponResponse;
import com.recharge.model.entity.Coupon;
import com.recharge.model.entity.UserCoupon;
import com.recharge.service.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    public List<CouponListResponse> getAvailableCoupons(Long userId) {
        // 查询上架的优惠券
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1)
                .eq(Coupon::getDeleted, 0)
                .orderByDesc(Coupon::getCreatedTime);
        List<Coupon> coupons = couponMapper.selectList(wrapper);

        return coupons.stream().map(coupon -> {
            CouponListResponse response = new CouponListResponse();
            response.setId(coupon.getId() != null ? coupon.getId().longValue() : null);
            response.setName(coupon.getName());
            response.setCouponType(coupon.getCouponType());
            response.setCouponTypeName(getCouponTypeName(coupon.getCouponType()));
            response.setDiscountAmount(coupon.getDiscountAmount());
            response.setDiscountRate(coupon.getDiscountRate());
            response.setMinAmount(coupon.getMinAmount());
            response.setDescription(coupon.getDescription());
            response.setValidDays(coupon.getValidDays());
            response.setStartTime(coupon.getStartTime());
            response.setEndTime(coupon.getEndTime());
            response.setPerUserLimit(coupon.getPerUserLimit());

            // 计算剩余数量
            int remainCount = coupon.getTotalCount() == 0 ? -1 : coupon.getTotalCount() - coupon.getUsedCount();
            response.setRemainCount(remainCount);

            // 查询用户已领取数量
            if (userId != null) {
                Long couponIdLong = coupon.getId() != null ? coupon.getId().longValue() : null;
                Integer receivedCount = userCouponMapper.countUserReceived(userId, couponIdLong);
                response.setReceivedCount(receivedCount);
                response.setReceived(receivedCount >= coupon.getPerUserLimit());
            } else {
                response.setReceivedCount(0);
                response.setReceived(false);
            }

            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveCoupon(Long userId, Long couponId) {
        // 获取优惠券模板
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getDeleted() == 1) {
            throw new BusinessException("优惠券不存在");
        }
        if (coupon.getStatus() != 1) {
            throw new BusinessException("优惠券已下架");
        }

        // 检查库存
        if (coupon.getTotalCount() > 0 && coupon.getUsedCount() >= coupon.getTotalCount()) {
            throw new BusinessException("优惠券已领完");
        }

        // 检查用户领取数量
        Integer receivedCount = userCouponMapper.countUserReceived(userId, couponId);
        if (receivedCount >= coupon.getPerUserLimit()) {
            throw new BusinessException("您已达到领取上限");
        }

        // 创建用户优惠券
        UserCoupon userCoupon = createUserCoupon(userId, coupon, 1);
        userCouponMapper.insert(userCoupon);

        // 更新已领取数量
        LambdaUpdateWrapper<Coupon> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Coupon::getId, couponId)
                .setSql("used_count = used_count + 1");
        couponMapper.update(null, updateWrapper);

        log.info("用户{}领取优惠券{}成功", userId, couponId);
    }

    @Override
    public Page<CouponResponse> getMyCoupons(Long userId, Integer status, Integer page, Integer size) {
        Page<UserCoupon> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getDeleted, 0);

        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }

        wrapper.orderByDesc(UserCoupon::getCreatedTime);

        Page<UserCoupon> resultPage = userCouponMapper.selectPage(pageParam, wrapper);

        // 转换响应
        Page<CouponResponse> responsePage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<CouponResponse> records = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(records);

        return responsePage;
    }

    @Override
    public List<CouponResponse> getUsableCoupons(Long userId, BigDecimal orderAmount) {
        // 查询用户未使用且未过期的优惠券
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, 0)
                .gt(UserCoupon::getValidEndTime, LocalDateTime.now())
                .le(UserCoupon::getMinAmount, orderAmount)
                .eq(UserCoupon::getDeleted, 0)
                .orderByDesc(UserCoupon::getDiscountAmount);

        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);

        return userCoupons.stream()
                .map(this::convertToResponse)
                .peek(response -> response.setAvailable(true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal useCoupon(Long userCouponId, Long orderId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || userCoupon.getDeleted() == 1) {
            throw new BusinessException("优惠券不存在");
        }
        if (userCoupon.getStatus() != 0) {
            throw new BusinessException("优惠券已使用或已过期");
        }
        if (userCoupon.getValidEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("优惠券已过期");
        }

        // 更新优惠券状态
        userCoupon.setStatus(1);
        userCoupon.setUsedTime(LocalDateTime.now());
        userCoupon.setUsedOrderId(orderId);
        userCouponMapper.updateById(userCoupon);

        return userCoupon.getDiscountAmount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnCoupon(Long orderId) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUsedOrderId, orderId)
                .eq(UserCoupon::getStatus, 1);

        UserCoupon userCoupon = userCouponMapper.selectOne(wrapper);
        if (userCoupon != null) {
            // 如果优惠券未过期，则退还
            if (userCoupon.getValidEndTime().isAfter(LocalDateTime.now())) {
                userCoupon.setStatus(0);
                userCoupon.setUsedTime(null);
                userCoupon.setUsedOrderId(null);
                userCouponMapper.updateById(userCoupon);
                log.info("退还优惠券成功，orderId={}, couponId={}", orderId, userCoupon.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantCoupon(Long userId, Long couponId, Integer source) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getDeleted() == 1 || coupon.getStatus() != 1) {
            log.warn("发放优惠券失败，优惠券不存在或已下架: couponId={}", couponId);
            return;
        }

        UserCoupon userCoupon = createUserCoupon(userId, coupon, source);
        userCouponMapper.insert(userCoupon);

        log.info("系统发放优惠券成功，userId={}, couponId={}, source={}", userId, couponId, source);
    }

    @Override
    public Integer countAvailable(Long userId) {
        return userCouponMapper.countAvailable(userId);
    }

    @Override
    public UserCoupon getUserCouponById(Long userCouponId) {
        return userCouponMapper.selectById(userCouponId);
    }

    /**
     * 创建用户优惠券
     */
    private UserCoupon createUserCoupon(Long userId, Coupon coupon, Integer source) {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(coupon.getId() != null ? coupon.getId().longValue() : null);
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setCouponType(coupon.getCouponType());
        userCoupon.setDiscountAmount(coupon.getDiscountAmount());
        userCoupon.setDiscountRate(coupon.getDiscountRate());
        userCoupon.setMinAmount(coupon.getMinAmount());
        userCoupon.setSource(source);
        userCoupon.setStatus(0);

        // 设置有效期
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidDays() != null && coupon.getValidDays() > 0) {
            userCoupon.setValidStartTime(now);
            userCoupon.setValidEndTime(now.plusDays(coupon.getValidDays()));
        } else if (coupon.getStartTime() != null && coupon.getEndTime() != null) {
            userCoupon.setValidStartTime(coupon.getStartTime());
            userCoupon.setValidEndTime(coupon.getEndTime());
        } else {
            userCoupon.setValidStartTime(now);
            userCoupon.setValidEndTime(now.plusDays(30));
        }

        return userCoupon;
    }

    /**
     * 转换为响应对象
     */
    private CouponResponse convertToResponse(UserCoupon userCoupon) {
        CouponResponse response = new CouponResponse();
        response.setId(userCoupon.getId());
        response.setCouponId(userCoupon.getCouponId());
        response.setName(userCoupon.getCouponName());
        response.setCouponType(userCoupon.getCouponType());
        response.setCouponTypeName(getCouponTypeName(userCoupon.getCouponType()));
        response.setDiscountAmount(userCoupon.getDiscountAmount());
        response.setDiscountRate(userCoupon.getDiscountRate());
        response.setMinAmount(userCoupon.getMinAmount());
        response.setValidStartTime(userCoupon.getValidStartTime());
        response.setValidEndTime(userCoupon.getValidEndTime());
        response.setStatus(userCoupon.getStatus());
        response.setStatusName(getStatusName(userCoupon.getStatus()));

        // 计算是否可用和剩余天数
        LocalDateTime now = LocalDateTime.now();
        boolean available = userCoupon.getStatus() == 0 && userCoupon.getValidEndTime().isAfter(now);
        response.setAvailable(available);

        if (available) {
            long days = ChronoUnit.DAYS.between(now, userCoupon.getValidEndTime());
            response.setRemainDays((int) days);
        } else {
            response.setRemainDays(0);
        }

        return response;
    }

    private String getCouponTypeName(Integer type) {
        if (type == null) return "";
        return switch (type) {
            case 1 -> "满减券";
            case 2 -> "折扣券";
            case 3 -> "无门槛券";
            default -> "";
        };
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "未使用";
            case 1 -> "已使用";
            case 2 -> "已过期";
            default -> "";
        };
    }
}
