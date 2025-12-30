package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.common.result.Result;
import com.recharge.mapper.SystemConfigMapper;
import com.recharge.model.entity.SystemConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 系统设置控制器
 */
@Tag(name = "管理后台-系统设置")
@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final SystemConfigMapper systemConfigMapper;

    @Operation(summary = "获取所有设置")
    @GetMapping
    public Result<Map<String, Object>> getSettings() {
        List<SystemConfig> configs = systemConfigMapper.selectList(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getDeleted, 0)
        );

        Map<String, Object> settings = new HashMap<>();

        // 基础设置
        Map<String, Object> basic = new HashMap<>();
        basic.put("siteName", getConfigValue(configs, "site_name", "话费充值平台"));
        basic.put("siteDesc", getConfigValue(configs, "site_desc", ""));
        basic.put("siteLogo", getConfigValue(configs, "site_logo", ""));
        basic.put("customerServiceUrl", getConfigValue(configs, "customer_service_url", ""));
        basic.put("shareTitle", getConfigValue(configs, "share_title", ""));
        basic.put("shareDesc", getConfigValue(configs, "share_desc", ""));
        basic.put("shareImage", getConfigValue(configs, "share_image", ""));
        settings.put("basic", basic);

        // 充值设置
        Map<String, Object> recharge = new HashMap<>();
        recharge.put("minRechargeAmount", getConfigValue(configs, "min_recharge_amount", "10"));
        recharge.put("maxRechargeAmount", getConfigValue(configs, "max_recharge_amount", "5000"));
        recharge.put("orderTimeout", getConfigValue(configs, "order_timeout", "30"));
        recharge.put("autoRefund", getConfigValue(configs, "auto_refund", "true"));
        settings.put("recharge", recharge);

        // 提现设置
        Map<String, Object> withdraw = new HashMap<>();
        withdraw.put("withdrawFeeRate", getConfigValue(configs, "withdraw_fee_rate", "0.006"));
        withdraw.put("withdrawMinAmount", getConfigValue(configs, "withdraw_min_amount", "100"));
        withdraw.put("withdrawMaxAmount", getConfigValue(configs, "withdraw_max_amount", "50000"));
        withdraw.put("withdrawDailyLimit", getConfigValue(configs, "withdraw_daily_limit", "3"));
        settings.put("withdraw", withdraw);

        // 佣金设置
        Map<String, Object> commission = new HashMap<>();
        commission.put("commissionRateLevel1", getConfigValue(configs, "commission_rate_level1", "0.05"));
        commission.put("commissionRateLevel2", getConfigValue(configs, "commission_rate_level2", "0.03"));
        commission.put("commissionRateLevel3", getConfigValue(configs, "commission_rate_level3", "0.01"));
        commission.put("commissionEnabled", getConfigValue(configs, "commission_enabled", "true"));
        settings.put("commission", commission);

        // 余额宝设置
        Map<String, Object> yuebao = new HashMap<>();
        yuebao.put("yuebaoAnnualRate", getConfigValue(configs, "yuebao_annual_rate", "0.0365"));
        yuebao.put("yuebaoMinTransfer", getConfigValue(configs, "yuebao_min_transfer", "1"));
        yuebao.put("yuebaoEnabled", getConfigValue(configs, "yuebao_enabled", "true"));
        settings.put("yuebao", yuebao);

        return Result.success(settings);
    }

    @Operation(summary = "更新基础设置")
    @PutMapping("/basic")
    public Result<Void> updateBasic(@RequestBody BasicSettingsRequest request) {
        saveConfig("site_name", request.getSiteName(), "网站名称", "string");
        saveConfig("site_desc", request.getSiteDesc(), "网站描述", "string");
        saveConfig("site_logo", request.getSiteLogo(), "网站Logo", "string");
        saveConfig("customer_service_url", request.getCustomerServiceUrl(), "客服链接", "string");
        saveConfig("share_title", request.getShareTitle(), "分享标题", "string");
        saveConfig("share_desc", request.getShareDesc(), "分享描述", "string");
        saveConfig("share_image", request.getShareImage(), "分享图片", "string");
        return Result.success();
    }

    @Operation(summary = "更新充值设置")
    @PutMapping("/recharge")
    public Result<Void> updateRecharge(@RequestBody RechargeSettingsRequest request) {
        saveConfig("min_recharge_amount", request.getMinRechargeAmount(), "最低充值金额", "number");
        saveConfig("max_recharge_amount", request.getMaxRechargeAmount(), "最高充值金额", "number");
        saveConfig("order_timeout", request.getOrderTimeout(), "订单超时时间(分钟)", "number");
        saveConfig("auto_refund", request.getAutoRefund(), "充值失败自动退款", "boolean");
        return Result.success();
    }

    @Operation(summary = "更新提现设置")
    @PutMapping("/withdraw")
    public Result<Void> updateWithdraw(@RequestBody WithdrawSettingsRequest request) {
        saveConfig("withdraw_fee_rate", request.getWithdrawFeeRate(), "提现手续费比例", "number");
        saveConfig("withdraw_min_amount", request.getWithdrawMinAmount(), "最低提现金额", "number");
        saveConfig("withdraw_max_amount", request.getWithdrawMaxAmount(), "最高提现金额", "number");
        saveConfig("withdraw_daily_limit", request.getWithdrawDailyLimit(), "每日提现次数限制", "number");
        return Result.success();
    }

    @Operation(summary = "更新佣金设置")
    @PutMapping("/commission")
    public Result<Void> updateCommission(@RequestBody CommissionSettingsRequest request) {
        saveConfig("commission_rate_level1", request.getCommissionRateLevel1(), "一级佣金比例", "number");
        saveConfig("commission_rate_level2", request.getCommissionRateLevel2(), "二级佣金比例", "number");
        saveConfig("commission_rate_level3", request.getCommissionRateLevel3(), "三级佣金比例", "number");
        saveConfig("commission_enabled", request.getCommissionEnabled(), "是否启用佣金", "boolean");
        return Result.success();
    }

    @Operation(summary = "更新余额宝设置")
    @PutMapping("/yuebao")
    public Result<Void> updateYuebao(@RequestBody YuebaoSettingsRequest request) {
        saveConfig("yuebao_annual_rate", request.getYuebaoAnnualRate(), "余额宝年化利率", "number");
        saveConfig("yuebao_min_transfer", request.getYuebaoMinTransfer(), "最低转入金额", "number");
        saveConfig("yuebao_enabled", request.getYuebaoEnabled(), "是否启用余额宝", "boolean");
        return Result.success();
    }

    private String getConfigValue(List<SystemConfig> configs, String key, String defaultValue) {
        return configs.stream()
                .filter(c -> key.equals(c.getConfigKey()))
                .map(SystemConfig::getConfigValue)
                .findFirst()
                .orElse(defaultValue);
    }

    private void saveConfig(String key, String value, String desc, String type) {
        if (value == null) {
            return;
        }

        SystemConfig config = systemConfigMapper.selectByKey(key);
        if (config == null) {
            config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setConfigDesc(desc);
            config.setConfigType(type);
            config.setDeleted(0);
            config.setCreatedTime(LocalDateTime.now());
            config.setUpdatedTime(LocalDateTime.now());
            systemConfigMapper.insert(config);
        } else {
            config.setConfigValue(value);
            config.setUpdatedTime(LocalDateTime.now());
            systemConfigMapper.updateById(config);
        }
    }

    @Data
    public static class BasicSettingsRequest {
        private String siteName;
        private String siteDesc;
        private String siteLogo;
        private String customerServiceUrl;
        private String shareTitle;
        private String shareDesc;
        private String shareImage;
    }

    @Data
    public static class RechargeSettingsRequest {
        private String minRechargeAmount;
        private String maxRechargeAmount;
        private String orderTimeout;
        private String autoRefund;
    }

    @Data
    public static class WithdrawSettingsRequest {
        private String withdrawFeeRate;
        private String withdrawMinAmount;
        private String withdrawMaxAmount;
        private String withdrawDailyLimit;
    }

    @Data
    public static class CommissionSettingsRequest {
        private String commissionRateLevel1;
        private String commissionRateLevel2;
        private String commissionRateLevel3;
        private String commissionEnabled;
    }

    @Data
    public static class YuebaoSettingsRequest {
        private String yuebaoAnnualRate;
        private String yuebaoMinTransfer;
        private String yuebaoEnabled;
    }
}
