package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.AdminRolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色-权限关联Mapper - 对应原表 cloud_times_admin_role_permission
 */
@Mapper
public interface AdminRolePermissionMapper extends BaseMapper<AdminRolePermission> {

    /**
     * 根据角色ID查询权限关联
     */
    default List<AdminRolePermission> selectByRoleId(Integer roleId) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdminRolePermission>()
                .eq(AdminRolePermission::getRoleId, roleId));
    }
}
