package com.recharge.service.team;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.model.entity.CommissionRecord;
import com.recharge.model.entity.TeamRelation;
import com.recharge.model.entity.User;

import java.util.List;

/**
 * 团队服务接口
 */
public interface TeamService {

    /**
     * 创建团队关系
     *
     * @param userId   用户ID
     * @param parentId 上级用户ID
     */
    void createTeamRelation(Long userId, Long parentId);

    /**
     * 获取直属下级数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countDirectChildren(Long userId);

    /**
     * 获取所有下级数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countAllChildren(Long userId);

    /**
     * 获取上级链（用于佣金计算）
     *
     * @param userId   用户ID
     * @param maxLevel 最大层级
     * @return 上级关系列表
     */
    List<TeamRelation> getAncestors(Long userId, int maxLevel);

    /**
     * 获取团队成员
     *
     * @param userId 用户ID
     * @param level  层级 (1=一级, 2=二级, 3=三级, null=全部)
     * @return 成员列表
     */
    List<User> getTeamMembers(Long userId, Integer level);

    /**
     * 获取佣金记录
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<CommissionRecord> getCommissionRecords(Long userId, Integer pageNum, Integer pageSize);
}
