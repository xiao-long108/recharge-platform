package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 管理员-角色关联实体 - 对应原表 cloud_times_admin_admin_role
 */
@Data
@TableName("cloud_times_admin_admin_role")
public class AdminAdminRole {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 管理员ID
     */
    @TableField("admin_id")
    private Integer adminId;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Integer roleId;
}
