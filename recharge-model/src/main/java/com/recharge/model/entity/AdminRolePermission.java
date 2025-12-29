package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色-权限关联实体 - 对应原表 cloud_times_admin_role_permission
 */
@Data
@TableName("cloud_times_admin_role_permission")
public class AdminRolePermission {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Integer roleId;

    /**
     * 权限ID
     */
    @TableField("permission_id")
    private Integer permissionId;
}
