package com.recharge.service.sign.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.SignRecordMapper;
import com.recharge.mapper.SystemConfigMapper;
import com.recharge.model.dto.response.SignResultResponse;
import com.recharge.model.dto.response.SignStatusResponse;
import com.recharge.model.entity.SignRecord;
import com.recharge.model.entity.SystemConfig;
import com.recharge.service.sign.SignService;
import com.recharge.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 签到服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final SignRecordMapper signRecordMapper;
    private final SystemConfigMapper systemConfigMapper;
    private final UserService userService;

    @Override
    public SignStatusResponse getSignStatus(Long userId) {
        SignStatusResponse response = new SignStatusResponse();
        LocalDate today = LocalDate.now();

        // 检查今日是否已签到
        SignRecord todayRecord = signRecordMapper.selectByUserIdAndDate(userId, today);
        response.setSignedToday(todayRecord != null);

        // 获取连续签到天数
        SignRecord lastRecord = signRecordMapper.selectLastRecord(userId);
        int continuousDays = 0;
        if (lastRecord != null) {
            if (lastRecord.getSignDate().equals(today)) {
                continuousDays = lastRecord.getContinuousDays();
            } else if (lastRecord.getSignDate().equals(today.minusDays(1))) {
                continuousDays = lastRecord.getContinuousDays();
            }
        }
        response.setContinuousDays(continuousDays);

        // 累计签到天数
        Integer totalDays = signRecordMapper.countTotalDays(userId);
        response.setTotalDays(totalDays != null ? totalDays : 0);

        // 今日奖励金额
        int nextDay = continuousDays % 7 + 1;
        response.setTodayReward(getRewardAmount(nextDay));

        // 本周签到记录
        response.setWeekSigns(getWeekSigns(userId, today));

        // 签到奖励配置
        response.setRewardConfigs(getRewardConfigs());

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignResultResponse doSign(Long userId) {
        LocalDate today = LocalDate.now();

        // 检查是否已签到
        SignRecord existRecord = signRecordMapper.selectByUserIdAndDate(userId, today);
        if (existRecord != null) {
            throw new BusinessException("今日已签到，请明天再来");
        }

        // 获取连续签到天数
        SignRecord lastRecord = signRecordMapper.selectLastRecord(userId);
        int continuousDays = 1;
        if (lastRecord != null && lastRecord.getSignDate().equals(today.minusDays(1))) {
            continuousDays = lastRecord.getContinuousDays() + 1;
        }

        // 计算奖励
        int dayInCycle = ((continuousDays - 1) % 7) + 1;
        BigDecimal rewardAmount = getRewardAmount(dayInCycle);

        // 检查是否获得额外奖励（连续7天）
        boolean bonusReward = continuousDays > 0 && continuousDays % 7 == 0;
        BigDecimal bonusAmount = BigDecimal.ZERO;
        if (bonusReward) {
            bonusAmount = getBonusAmount();
        }

        // 创建签到记录
        SignRecord signRecord = new SignRecord();
        signRecord.setUserId(userId);
        signRecord.setSignDate(today);
        signRecord.setContinuousDays(continuousDays);
        signRecord.setRewardType(2); // 余额奖励
        signRecord.setRewardAmount(rewardAmount.add(bonusAmount));
        signRecord.setRemark("签到奖励" + (bonusReward ? "（含连续7天额外奖励）" : ""));
        signRecordMapper.insert(signRecord);

        // 发放奖励到用户余额
        BigDecimal totalReward = rewardAmount.add(bonusAmount);
        if (totalReward.compareTo(BigDecimal.ZERO) > 0) {
            userService.addBalance(userId, totalReward, signRecord.getId(), "签到奖励");
        }

        // 构建响应
        SignResultResponse response = new SignResultResponse();
        response.setSuccess(true);
        response.setContinuousDays(continuousDays);
        response.setRewardType(2);
        response.setRewardAmount(rewardAmount);
        response.setBonusReward(bonusReward);
        response.setBonusAmount(bonusAmount);
        response.setMessage(bonusReward
                ? String.format("签到成功！连续签到%d天，获得%.2f元+额外奖励%.2f元", continuousDays, rewardAmount, bonusAmount)
                : String.format("签到成功！连续签到%d天，获得%.2f元", continuousDays, rewardAmount));

        return response;
    }

    @Override
    public boolean hasSignedToday(Long userId) {
        SignRecord record = signRecordMapper.selectByUserIdAndDate(userId, LocalDate.now());
        return record != null;
    }

    /**
     * 获取本周签到记录
     */
    private List<SignStatusResponse.DaySign> getWeekSigns(Long userId, LocalDate today) {
        List<SignStatusResponse.DaySign> weekSigns = new ArrayList<>();

        // 获取本周一
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 查询本周签到记录
        LambdaQueryWrapper<SignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignRecord::getUserId, userId)
                .ge(SignRecord::getSignDate, monday)
                .le(SignRecord::getSignDate, today)
                .eq(SignRecord::getDeleted, 0);
        List<SignRecord> records = signRecordMapper.selectList(wrapper);

        // 构建签到记录Map
        Map<LocalDate, SignRecord> recordMap = new HashMap<>();
        for (SignRecord record : records) {
            recordMap.put(record.getSignDate(), record);
        }

        // 遍历本周每一天
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            SignStatusResponse.DaySign daySign = new SignStatusResponse.DaySign();
            daySign.setDate(date);

            SignRecord record = recordMap.get(date);
            if (record != null) {
                daySign.setSigned(true);
                daySign.setReward(record.getRewardAmount());
            } else {
                daySign.setSigned(false);
                daySign.setReward(getRewardAmount(i + 1));
            }

            weekSigns.add(daySign);
        }

        return weekSigns;
    }

    /**
     * 获取签到奖励配置
     */
    private List<SignStatusResponse.SignRewardConfig> getRewardConfigs() {
        List<SignStatusResponse.SignRewardConfig> configs = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            SignStatusResponse.SignRewardConfig config = new SignStatusResponse.SignRewardConfig();
            config.setDay(i);
            config.setReward(getRewardAmount(i));
            config.setIsBonus(false);
            configs.add(config);
        }
        // 添加额外奖励配置
        SignStatusResponse.SignRewardConfig bonusConfig = new SignStatusResponse.SignRewardConfig();
        bonusConfig.setDay(7);
        bonusConfig.setReward(getBonusAmount());
        bonusConfig.setIsBonus(true);
        configs.add(bonusConfig);
        return configs;
    }

    /**
     * 获取指定天数的奖励金额
     */
    private BigDecimal getRewardAmount(int day) {
        String configKey = "sign_reward_day" + day;
        SystemConfig config = systemConfigMapper.selectByKey(configKey);
        if (config != null) {
            try {
                return new BigDecimal(config.getConfigValue());
            } catch (NumberFormatException e) {
                log.warn("签到奖励配置值格式错误: {}", configKey);
            }
        }
        // 默认奖励
        BigDecimal[] defaults = {
                new BigDecimal("0.1"),
                new BigDecimal("0.2"),
                new BigDecimal("0.3"),
                new BigDecimal("0.5"),
                new BigDecimal("0.8"),
                new BigDecimal("1.0"),
                new BigDecimal("2.0")
        };
        return day > 0 && day <= 7 ? defaults[day - 1] : new BigDecimal("0.1");
    }

    /**
     * 获取连续7天额外奖励金额
     */
    private BigDecimal getBonusAmount() {
        SystemConfig config = systemConfigMapper.selectByKey("sign_continuous_bonus");
        if (config != null) {
            try {
                return new BigDecimal(config.getConfigValue());
            } catch (NumberFormatException e) {
                log.warn("连续签到奖励配置值格式错误");
            }
        }
        return new BigDecimal("5.0");
    }
}
