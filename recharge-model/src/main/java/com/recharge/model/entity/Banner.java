package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图实体 - 对应原表 cloud_times_api_image
 */
@Data
@TableName("cloud_times_api_image")
public class Banner {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 标题
     */
    private String title;

    /**
     * 排序 (降序)
     */
    private Integer sort;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 跳转地址
     */
    private String linkurl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    @TableLogic
    @TableField("delete_time")
    private LocalDateTime deleteTime;

    // ========== 原字段getter/setter ==========

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    // ========== 兼容方法 ==========

    /**
     * 获取链接类型 (老表没有此字段)
     */
    public Integer getLinkType() {
        return linkurl != null && !linkurl.isEmpty() ? 2 : 0;
    }

    /**
     * 设置链接类型 (老表没有此字段，忽略)
     */
    public void setLinkType(Integer linkType) {
        // 忽略
    }

    /**
     * 获取链接地址 (兼容linkUrl)
     */
    public String getLinkUrl() {
        return linkurl;
    }

    /**
     * 设置链接地址 (兼容linkUrl)
     */
    public void setLinkUrl(String linkUrl) {
        this.linkurl = linkUrl;
    }

    /**
     * 获取关联ID (老表没有此字段)
     */
    public Long getLinkId() {
        return null;
    }

    /**
     * 设置关联ID (老表没有此字段，忽略)
     */
    public void setLinkId(Long linkId) {
        // 忽略
    }

    /**
     * 获取展示位置 (老表没有此字段，根据type判断)
     */
    public String getPosition() {
        return type != null && type == 1 ? "home" : "other";
    }

    /**
     * 设置展示位置 (老表没有此字段，通过type实现)
     */
    public void setPosition(String position) {
        if ("home".equals(position)) {
            this.type = 1;
        } else {
            this.type = 2;
        }
    }

    /**
     * 获取排序 (兼容sortOrder)
     */
    public Integer getSortOrder() {
        return sort;
    }

    /**
     * 设置排序 (兼容sortOrder)
     */
    public void setSortOrder(Integer sortOrder) {
        this.sort = sortOrder;
    }

    /**
     * 获取开始展示时间 (老表没有此字段)
     */
    public LocalDateTime getStartTime() {
        return null;
    }

    /**
     * 设置开始展示时间 (老表没有此字段，忽略)
     */
    public void setStartTime(LocalDateTime startTime) {
        // 忽略
    }

    /**
     * 获取结束展示时间 (老表没有此字段)
     */
    public LocalDateTime getEndTime() {
        return null;
    }

    /**
     * 设置结束展示时间 (老表没有此字段，忽略)
     */
    public void setEndTime(LocalDateTime endTime) {
        // 忽略
    }

    /**
     * 获取状态 (根据delete_time判断)
     */
    public Integer getStatus() {
        return deleteTime == null ? 1 : 0;
    }

    /**
     * 设置状态 (老表没有此字段，忽略)
     */
    public void setStatus(Integer status) {
        // 忽略
    }

    /**
     * 获取删除状态 (根据delete_time判断)
     */
    public Integer getDeleted() {
        return deleteTime != null ? 1 : 0;
    }

    /**
     * 设置删除状态 (通过delete_time实现)
     */
    public void setDeleted(Integer deleted) {
        if (deleted != null && deleted == 1) {
            this.deleteTime = LocalDateTime.now();
        } else {
            this.deleteTime = null;
        }
    }

    /**
     * 兼容 createdTime 字段
     */
    public LocalDateTime getCreatedTime() {
        return createTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createTime = createdTime;
    }

    /**
     * 兼容 updatedTime 字段
     */
    public LocalDateTime getUpdatedTime() {
        return updateTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updateTime = updatedTime;
    }
}
