package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员Mapper - 对应原表 cloud_times_admin_admin
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据用户名查询管理员
     */
    default Admin selectByUsername(String username) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username));
    }
}
