package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体 - 对应表 cloud_times_api_config
 */
@Data
@TableName("cloud_times_api_config")
public class SystemConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置描述
     */
    @TableField("config_desc")
    private String configDesc;

    /**
     * 值类型: string/number/json/boolean
     */
    @TableField("config_type")
    private String configType;

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

    public LocalDateTime getCreatedTime() {
        return createTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updateTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updateTime = updatedTime;
    }

    public Integer getDeleted() {
        return deleteTime != null ? 1 : 0;
    }

    public void setDeleted(Integer deleted) {
        if (deleted != null && deleted == 1) {
            this.deleteTime = LocalDateTime.now();
        } else {
            this.deleteTime = null;
        }
    }
}
