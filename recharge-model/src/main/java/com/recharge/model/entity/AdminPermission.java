package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限/菜单实体 - 对应原表 cloud_times_admin_permission
 */
@Data
@TableName("cloud_times_admin_permission")
public class AdminPermission {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 父级ID
     */
    private Integer pid;

    /**
     * 名称
     */
    private String title;

    /**
     * 地址
     */
    private String href;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 类型 0=目录 1=菜单
     */
    private Integer type;

    /**
     * 1=管理员 2=代理
     */
    private Integer stype;

    /**
     * 状态 1=启用
     */
    private Integer status;
}
