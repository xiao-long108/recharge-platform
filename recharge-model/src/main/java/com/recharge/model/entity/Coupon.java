package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 优惠券实体 - 对应原表 cloud_times_api_voucher
 */
@Data
@TableName("cloud_times_api_voucher")
public class Coupon {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 类型: 1-话费充值优惠券, 2-开店优惠券, 3-铺货优惠券
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    @TableField("`desc`")
    private String description;

    /**
     * 金额
     */
    private Integer amount;

    /**
     * 图片
     */
    private String image;

    /**
     * 适用产品ID
     */
    @TableField("product_id")
    private Integer productId;

    /**
     * 方式: 1-邀请用户, 2-下级完成激活
     */
    private Integer method;

    /**
     * 任务数量
     */
    private Integer num;

    /**
     * 开始时间
     */
    private LocalDate starttime;

    /**
     * 结束时间
     */
    private LocalDate endtime;

    /**
     * 添加时间(时间戳)
     */
    private Integer addtime;

    // ========== 原字段getter/setter (供LambdaQueryWrapper使用) ==========

    public LocalDate getStarttime() {
        return starttime;
    }

    public void setStarttime(LocalDate starttime) {
        this.starttime = starttime;
    }

    public LocalDate getEndtime() {
        return endtime;
    }

    public void setEndtime(LocalDate endtime) {
        this.endtime = endtime;
    }

    // ========== 兼容方法 ==========

    /**
     * 获取优惠券类型 (兼容couponType)
     */
    public Integer getCouponType() {
        return type;
    }

    /**
     * 设置优惠券类型 (兼容couponType)
     */
    public void setCouponType(Integer couponType) {
        this.type = couponType;
    }

    /**
     * 获取优惠金额 (兼容discountAmount)
     */
    public BigDecimal getDiscountAmount() {
        return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
    }

    /**
     * 设置优惠金额 (兼容discountAmount)
     */
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.amount = discountAmount != null ? discountAmount.intValue() : 0;
    }

    /**
     * 获取折扣比例 (老表没有此字段)
     */
    public BigDecimal getDiscountRate() {
        return null;
    }

    /**
     * 设置折扣比例 (老表没有此字段，忽略)
     */
    public void setDiscountRate(BigDecimal discountRate) {
        // 忽略
    }

    /**
     * 获取使用门槛金额 (老表没有此字段)
     */
    public BigDecimal getMinAmount() {
        return BigDecimal.ZERO;
    }

    /**
     * 设置使用门槛金额 (老表没有此字段，忽略)
     */
    public void setMinAmount(BigDecimal minAmount) {
        // 忽略
    }

    /**
     * 获取最高优惠金额 (老表没有此字段)
     */
    public BigDecimal getMaxDiscount() {
        return null;
    }

    /**
     * 设置最高优惠金额 (老表没有此字段，忽略)
     */
    public void setMaxDiscount(BigDecimal maxDiscount) {
        // 忽略
    }

    /**
     * 获取发放总量 (兼容totalCount)
     */
    public Integer getTotalCount() {
        return num != null ? num : 0;
    }

    /**
     * 设置发放总量 (兼容totalCount)
     */
    public void setTotalCount(Integer totalCount) {
        this.num = totalCount;
    }

    /**
     * 获取已领取数量 (老表没有此字段)
     */
    public Integer getUsedCount() {
        return 0;
    }

    /**
     * 设置已领取数量 (老表没有此字段，忽略)
     */
    public void setUsedCount(Integer usedCount) {
        // 忽略
    }

    /**
     * 获取每人限领数量 (老表没有此字段)
     */
    public Integer getPerUserLimit() {
        return 1;
    }

    /**
     * 设置每人限领数量 (老表没有此字段，忽略)
     */
    public void setPerUserLimit(Integer perUserLimit) {
        // 忽略
    }

    /**
     * 获取有效天数 (老表没有此字段)
     */
    public Integer getValidDays() {
        return null;
    }

    /**
     * 设置有效天数 (老表没有此字段，忽略)
     */
    public void setValidDays(Integer validDays) {
        // 忽略
    }

    /**
     * 获取开始时间 (兼容startTime)
     */
    public LocalDateTime getStartTime() {
        return starttime != null ? starttime.atStartOfDay() : null;
    }

    /**
     * 设置开始时间 (兼容startTime)
     */
    public void setStartTime(LocalDateTime startTime) {
        this.starttime = startTime != null ? startTime.toLocalDate() : null;
    }

    /**
     * 获取结束时间 (兼容endTime)
     */
    public LocalDateTime getEndTime() {
        return endtime != null ? endtime.atStartOfDay() : null;
    }

    /**
     * 设置结束时间 (兼容endTime)
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endtime = endTime != null ? endTime.toLocalDate() : null;
    }

    /**
     * 获取使用范围 (老表没有此字段)
     */
    public Integer getUseScope() {
        return productId != null && productId > 0 ? 1 : 0;
    }

    /**
     * 设置使用范围 (老表没有此字段，忽略)
     */
    public void setUseScope(Integer useScope) {
        // 忽略
    }

    /**
     * 获取范围ID列表 (老表没有此字段)
     */
    public String getScopeIds() {
        return productId != null && productId > 0 ? String.valueOf(productId) : null;
    }

    /**
     * 设置范围ID列表 (老表没有此字段，忽略)
     */
    public void setScopeIds(String scopeIds) {
        // 忽略
    }

    /**
     * 获取状态 (老表没有此字段，默认1=启用)
     */
    public Integer getStatus() {
        return 1;
    }

    /**
     * 设置状态 (老表没有此字段，忽略)
     */
    public void setStatus(Integer status) {
        // 忽略
    }

    /**
     * 获取删除状态 (老表没有此字段)
     */
    public Integer getDeleted() {
        return 0;
    }

    /**
     * 设置删除状态 (老表没有此字段，忽略)
     */
    public void setDeleted(Integer deleted) {
        // 忽略
    }

    /**
     * 获取创建时间 (兼容createdTime)
     */
    public LocalDateTime getCreatedTime() {
        if (addtime != null && addtime > 0) {
            return LocalDateTime.ofEpochSecond(addtime, 0, java.time.ZoneOffset.ofHours(8));
        }
        return null;
    }

    /**
     * 设置创建时间 (兼容createdTime)
     */
    public void setCreatedTime(LocalDateTime createdTime) {
        if (createdTime != null) {
            this.addtime = (int) createdTime.toEpochSecond(java.time.ZoneOffset.ofHours(8));
        }
    }

    /**
     * 获取更新时间 (老表没有此字段)
     */
    public LocalDateTime getUpdatedTime() {
        return getCreatedTime();
    }

    /**
     * 设置更新时间 (老表没有此字段，忽略)
     */
    public void setUpdatedTime(LocalDateTime updatedTime) {
        // 忽略
    }
}
