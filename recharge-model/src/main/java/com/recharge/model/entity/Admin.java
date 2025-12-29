package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体 - 对应原表 cloud_times_admin_admin
 */
@Data
@TableName("cloud_times_admin_admin")
public class Admin {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 管理员绑定ID
     */
    @TableField("admin_bind_id")
    private Integer adminBindId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码 (MD5)
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态 1=正常
     */
    private Integer status;

    /**
     * 类型 1=管理员 2=代理
     */
    private Integer stype;

    /**
     * 分组
     */
    @TableField("`group`")
    private String group;

    /**
     * 错误次数
     */
    @TableField("error_times")
    private Integer errorTimes;

    /**
     * IP
     */
    private String ip;

    /**
     * Token
     */
    private String token;

    /**
     * 子账号
     */
    private String subs;

    /**
     * 子账号总数
     */
    private Integer subtotal;

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
}
