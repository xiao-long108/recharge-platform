package com.recharge.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.CommissionRecordMapper;
import com.recharge.mapper.DistributionLevelMapper;
import com.recharge.mapper.TeamRelationMapper;
import com.recharge.mapper.UserDistributionMapper;
import com.recharge.model.dto.request.DistributionUpgradeRequest;
import com.recharge.model.dto.response.DistributionLevelResponse;
import com.recharge.model.entity.CommissionRecord;
import com.recharge.model.entity.DistributionLevel;
import com.recharge.model.entity.TeamRelation;
import com.recharge.model.entity.UserDistribution;
import com.recharge.service.distribution.DistributionService;
import com.recharge.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 分销服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributionServiceImpl implements DistributionService {

    private final DistributionLevelMapper distributionLevelMapper;
    private final UserDistributionMapper userDistributionMapper;
    private final CommissionRecordMapper commissionRecordMapper;
    private final TeamRelationMapper teamRelationMapper;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public List<DistributionLevelResponse> getLevelList(Long userId) {
        // 获取用户当前等级
        UserDistribution userDistribution = userDistributionMapper.selectByUserId(userId);
        int currentLevel = userDistribution != null ? userDistribution.getLevel() : 0;

        // 查询所有等级
        LambdaQueryWrapper<DistributionLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DistributionLevel::getStatus, 1)
                .eq(DistributionLevel::getDeleted, 0)
                .orderByAsc(DistributionLevel::getSortOrder);
        List<DistributionLevel> levels = distributionLevelMapper.selectList(wrapper);

        List<DistributionLevelResponse> responses = new ArrayList<>();
        for (DistributionLevel level : levels) {
            DistributionLevelResponse response = new DistributionLevelResponse();
            response.setId(level.getId());
            response.setName(level.getName());
            response.setLevel(level.getLevel());
            response.setIcon(level.getIcon());
            response.setPrice(level.getPrice());
            response.setCommissionRate1(level.getCommissionRate1());
            response.setCommissionRate2(level.getCommissionRate2());
            response.setCommissionRate3(level.getCommissionRate3());

            // 解析特权列表
            try {
                if (level.getPrivileges() != null) {
                    List<String> privileges = objectMapper.readValue(level.getPrivileges(), new TypeReference<>() {});
                    response.setPrivileges(privileges);
                }
            } catch (Exception e) {
                response.setPrivileges(new ArrayList<>());
            }

            response.setCurrent(level.getLevel() == currentLevel);
            response.setCanUpgrade(level.getLevel() > currentLevel);

            responses.add(response);
        }

        return responses;
    }

    @Override
    public UserDistribution getUserDistribution(Long userId) {
        return userDistributionMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upgradLevel(Long userId, DistributionUpgradeRequest request) {
        // 验证支付密码
        if (!userService.verifyPayPassword(userId, request.getPayPassword())) {
            throw new BusinessException("支付密码错误");
        }

        // 获取目标等级
        DistributionLevel targetLevel = distributionLevelMapper.selectById(request.getLevelId());
        if (targetLevel == null || targetLevel.getStatus() != 1) {
            throw new BusinessException("等级不存在或已禁用");
        }

        // 获取当前等级
        UserDistribution currentDistribution = userDistributionMapper.selectByUserId(userId);
        int currentLevel = currentDistribution != null ? currentDistribution.getLevel() : 0;

        if (targetLevel.getLevel() <= currentLevel) {
            throw new BusinessException("不能降级或购买同级等级");
        }

        // 扣减余额
        if (targetLevel.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            userService.deductBalance(userId, targetLevel.getPrice(), targetLevel.getId(), "分销等级升级：" + targetLevel.getName());
        }

        // 更新或创建用户分销等级
        if (currentDistribution != null) {
            currentDistribution.setLevelId(targetLevel.getId());
            currentDistribution.setLevel(targetLevel.getLevel());
            currentDistribution.setUpgradeTime(LocalDateTime.now());
            userDistributionMapper.updateById(currentDistribution);
        } else {
            UserDistribution newDistribution = new UserDistribution();
            newDistribution.setUserId(userId);
            newDistribution.setLevelId(targetLevel.getId());
            newDistribution.setLevel(targetLevel.getLevel());
            newDistribution.setUpgradeTime(LocalDateTime.now());
            newDistribution.setStatus(1);
            userDistributionMapper.insert(newDistribution);
        }

        log.info("用户{}升级分销等级到{}成功", userId, targetLevel.getName());
    }

    @Override
    public DistributionCenterResponse getDistributionCenter(Long userId) {
        // 获取用户分销等级
        UserDistribution userDistribution = userDistributionMapper.selectByUserId(userId);

        String levelName = "普通会员";
        Integer level = 0;
        String levelIcon = null;
        BigDecimal commissionRate1 = new BigDecimal("0.03");
        BigDecimal commissionRate2 = new BigDecimal("0.02");
        BigDecimal commissionRate3 = new BigDecimal("0.01");

        if (userDistribution != null) {
            DistributionLevel distributionLevel = distributionLevelMapper.selectById(userDistribution.getLevelId());
            if (distributionLevel != null) {
                levelName = distributionLevel.getName();
                level = distributionLevel.getLevel();
                levelIcon = distributionLevel.getIcon();
                commissionRate1 = distributionLevel.getCommissionRate1();
                commissionRate2 = distributionLevel.getCommissionRate2();
                commissionRate3 = distributionLevel.getCommissionRate3();
            }
        }

        // 统计总佣金
        LambdaQueryWrapper<CommissionRecord> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(CommissionRecord::getUserId, userId)
                .eq(CommissionRecord::getStatus, 1)
                .eq(CommissionRecord::getDeleted, 0);
        List<CommissionRecord> allRecords = commissionRecordMapper.selectList(totalWrapper);
        BigDecimal totalCommission = allRecords.stream()
                .map(CommissionRecord::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 统计今日佣金
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<CommissionRecord> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(CommissionRecord::getUserId, userId)
                .eq(CommissionRecord::getStatus, 1)
                .ge(CommissionRecord::getCreatedTime, todayStart)
                .eq(CommissionRecord::getDeleted, 0);
        List<CommissionRecord> todayRecords = commissionRecordMapper.selectList(todayWrapper);
        BigDecimal todayCommission = todayRecords.stream()
                .map(CommissionRecord::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 统计团队人数
        LambdaQueryWrapper<TeamRelation> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.eq(TeamRelation::getParentId, userId)
                .eq(TeamRelation::getDeleted, 0);
        Long directCount = teamRelationMapper.selectCount(teamWrapper);

        // 统计所有下级人数
        LambdaQueryWrapper<TeamRelation> allTeamWrapper = new LambdaQueryWrapper<>();
        allTeamWrapper.like(TeamRelation::getAncestorPath, "," + userId + ",")
                .eq(TeamRelation::getDeleted, 0);
        Long teamCount = teamRelationMapper.selectCount(allTeamWrapper);

        return new DistributionCenterResponse(
                levelName,
                level,
                levelIcon,
                commissionRate1,
                commissionRate2,
                commissionRate3,
                totalCommission,
                todayCommission,
                teamCount.intValue(),
                directCount.intValue()
        );
    }
}
