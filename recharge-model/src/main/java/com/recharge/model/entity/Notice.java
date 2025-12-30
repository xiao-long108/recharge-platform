package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体 - 对应原表 cloud_times_api_journalism
 */
@Data
@TableName("cloud_times_api_journalism")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 图片
     */
    private String image;

    /**
     * 排序 (降序)
     */
    private Integer sort;

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

    // ========== 兼容方法 ==========

    /**
     * 获取公告类型 (老表没有此字段，返回1=系统公告)
     */
    public Integer getNoticeType() {
        return 1;
    }

    /**
     * 设置公告类型 (老表没有此字段，忽略)
     */
    public void setNoticeType(Integer noticeType) {
        // 忽略
    }

    /**
     * 获取是否置顶 (老表没有此字段，根据sort判断)
     */
    public Integer getIsTop() {
        return sort != null && sort > 0 ? 1 : 0;
    }

    /**
     * 设置是否置顶 (老表没有此字段，通过sort实现)
     */
    public void setIsTop(Integer isTop) {
        if (isTop != null && isTop == 1) {
            this.sort = 999;
        }
    }

    /**
     * 获取浏览次数 (老表没有此字段，返回0)
     */
    public Integer getViewCount() {
        return 0;
    }

    /**
     * 设置浏览次数 (老表没有此字段，忽略)
     */
    public void setViewCount(Integer viewCount) {
        // 忽略
    }

    /**
     * 获取开始展示时间 (老表没有此字段，返回null)
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
     * 获取结束展示时间 (老表没有此字段，返回null)
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
     * 获取状态 (老表没有此字段，根据delete_time判断)
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
