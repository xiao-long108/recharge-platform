package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.AdminPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限/菜单Mapper - 对应原表 cloud_times_admin_permission
 */
@Mapper
public interface AdminPermissionMapper extends BaseMapper<AdminPermission> {

    /**
     * 根据管理员ID查询权限列表
     */
    @Select("SELECT DISTINCT p.* FROM cloud_times_admin_permission p " +
            "INNER JOIN cloud_times_admin_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN cloud_times_admin_admin_role ar ON rp.role_id = ar.role_id " +
            "WHERE ar.admin_id = #{adminId} AND p.status = 1 " +
            "ORDER BY p.sort ASC")
    List<AdminPermission> selectByAdminId(@Param("adminId") Long adminId);

    /**
     * 查询所有启用的菜单权限
     */
    default List<AdminPermission> selectAllEnabled() {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdminPermission>()
                .eq(AdminPermission::getStatus, 1)
                .orderByAsc(AdminPermission::getSort));
    }
}
