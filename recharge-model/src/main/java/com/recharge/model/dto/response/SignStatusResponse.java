package com.recharge.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 签到状态响应
 */
@Data
public class SignStatusResponse {

    /**
     * 今日是否已签到
     */
    private Boolean signedToday;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 累计签到天数
     */
    private Integer totalDays;

    /**
     * 今日奖励金额
     */
    private BigDecimal todayReward;

    /**
     * 本周签到记录
     */
    private List<DaySign> weekSigns;

    /**
     * 签到奖励配置
     */
    private List<SignRewardConfig> rewardConfigs;

    @Data
    public static class DaySign {
        private LocalDate date;
        private Boolean signed;
        private BigDecimal reward;
    }

    @Data
    public static class SignRewardConfig {
        private Integer day;
        private BigDecimal reward;
        private Boolean isBonus;
    }
}
