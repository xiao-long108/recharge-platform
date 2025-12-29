package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.AdminRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper - 对应原表 cloud_times_admin_role
 */
@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {
}
