package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.AdminAdminRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 管理员-角色关联Mapper - 对应原表 cloud_times_admin_admin_role
 */
@Mapper
public interface AdminAdminRoleMapper extends BaseMapper<AdminAdminRole> {

    /**
     * 根据管理员ID查询角色关联
     */
    default List<AdminAdminRole> selectByAdminId(Integer adminId) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdminAdminRole>()
                .eq(AdminAdminRole::getAdminId, adminId));
    }
}
