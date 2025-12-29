package com.recharge.service.team.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.mapper.CommissionRecordMapper;
import com.recharge.mapper.TeamRelationMapper;
import com.recharge.mapper.UserMapper;
import com.recharge.model.entity.CommissionRecord;
import com.recharge.model.entity.TeamRelation;
import com.recharge.model.entity.User;
import com.recharge.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 团队服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRelationMapper teamRelationMapper;
    private final UserMapper userMapper;
    private final CommissionRecordMapper commissionRecordMapper;

    @Override
    public void createTeamRelation(Long userId, Long parentId) {
        // 查询父级的团队关系
        TeamRelation parentRelation = teamRelationMapper.selectByUserId(parentId);

        TeamRelation relation = new TeamRelation();
        relation.setUserId(userId);
        relation.setParentId(parentId);
        relation.setDeleted(0);

        if (parentRelation != null) {
            // 继承父级的祖先路径
            String ancestorPath = parentRelation.getAncestorPath();
            if (ancestorPath == null || ancestorPath.isEmpty()) {
                relation.setAncestorPath("," + parentId + ",");
            } else {
                relation.setAncestorPath(ancestorPath + parentId + ",");
            }
            relation.setLevel(parentRelation.getLevel() + 1);
        } else {
            relation.setAncestorPath("," + parentId + ",");
            relation.setLevel(1);
        }

        teamRelationMapper.insert(relation);
        log.info("创建团队关系: userId={}, parentId={}, level={}", userId, parentId, relation.getLevel());
    }

    @Override
    public int countDirectChildren(Long userId) {
        return teamRelationMapper.countDirectChildren(userId);
    }

    @Override
    public int countAllChildren(Long userId) {
        return teamRelationMapper.countAllChildren(userId);
    }

    @Override
    public List<TeamRelation> getAncestors(Long userId, int maxLevel) {
        TeamRelation relation = teamRelationMapper.selectByUserId(userId);
        if (relation == null || relation.getAncestorPath() == null) {
            return new ArrayList<>();
        }

        // 解析祖先路径
        String ancestorPath = relation.getAncestorPath();
        String[] ancestors = ancestorPath.split(",");
        List<TeamRelation> result = new ArrayList<>();

        // 从最近的上级开始
        int count = 0;
        for (int i = ancestors.length - 1; i >= 0 && count < maxLevel; i--) {
            if (!ancestors[i].isEmpty()) {
                Long ancestorId = Long.parseLong(ancestors[i]);
                TeamRelation ancestor = new TeamRelation();
                ancestor.setUserId(ancestorId);
                ancestor.setParentId(ancestorId);
                ancestor.setLevel(count + 1);
                result.add(ancestor);
                count++;
            }
        }

        return result;
    }

    @Override
    public List<User> getTeamMembers(Long userId, Integer level) {
        // 查询下级关系
        LambdaQueryWrapper<TeamRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamRelation::getParentId, userId);

        if (level != null && level == 1) {
            // 一级成员：直接下级
            wrapper.eq(TeamRelation::getLevel, 1);
        } else if (level != null && level > 1) {
            // 二级/三级成员：通过祖先路径查询
            wrapper = new LambdaQueryWrapper<>();
            wrapper.like(TeamRelation::getAncestorPath, "," + userId + ",");
            wrapper.eq(TeamRelation::getLevel, level);
        }

        List<TeamRelation> relations = teamRelationMapper.selectList(wrapper);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取用户ID列表
        List<Long> userIds = relations.stream()
                .map(TeamRelation::getUserId)
                .collect(Collectors.toList());

        // 批量查询用户信息
        return userMapper.selectBatchIds(userIds);
    }

    @Override
    public Page<CommissionRecord> getCommissionRecords(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<CommissionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommissionRecord::getUserId, userId);
        wrapper.orderByDesc(CommissionRecord::getCreatedTime);

        return commissionRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
