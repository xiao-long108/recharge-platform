-- ============================================================
-- 创建系统配置表 (老数据库没有此表，需要创建)
-- 请在 huafei_api 数据库中执行此脚本
-- ============================================================

USE huafei_api;

-- 系统配置表
CREATE TABLE IF NOT EXISTS `cloud_times_api_config` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` varchar(100) NOT NULL COMMENT '配置键',
    `config_value` text NOT NULL COMMENT '配置值',
    `config_desc` varchar(500) DEFAULT NULL COMMENT '配置描述',
    `config_type` varchar(20) NOT NULL DEFAULT 'string' COMMENT '值类型: string/number/json/boolean',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
    `delete_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始化系统配置数据
INSERT INTO `cloud_times_api_config` (`config_key`, `config_value`, `config_desc`, `config_type`, `create_time`, `update_time`) VALUES
('site_name', '话费充值平台', '网站名称', 'string', NOW(), NOW()),
('site_desc', '便捷快速的话费充值服务', '网站描述', 'string', NOW(), NOW()),
('site_logo', '', '网站Logo', 'string', NOW(), NOW()),
('customer_service_url', '', '客服链接', 'string', NOW(), NOW()),
('share_title', '话费充值低至9.5折', '分享标题', 'string', NOW(), NOW()),
('share_desc', '快来一起省钱充话费', '分享描述', 'string', NOW(), NOW()),
('share_image', '', '分享图片', 'string', NOW(), NOW()),
('min_recharge_amount', '10', '最低充值金额', 'number', NOW(), NOW()),
('max_recharge_amount', '5000', '最高充值金额', 'number', NOW(), NOW()),
('order_timeout', '30', '订单超时时间(分钟)', 'number', NOW(), NOW()),
('auto_refund', 'true', '充值失败自动退款', 'boolean', NOW(), NOW()),
('withdraw_fee_rate', '0.006', '提现手续费比例', 'number', NOW(), NOW()),
('withdraw_min_amount', '100', '最低提现金额', 'number', NOW(), NOW()),
('withdraw_max_amount', '50000', '最高提现金额', 'number', NOW(), NOW()),
('withdraw_daily_limit', '3', '每日提现次数限制', 'number', NOW(), NOW()),
('commission_rate_level1', '0.05', '一级佣金比例', 'number', NOW(), NOW()),
('commission_rate_level2', '0.03', '二级佣金比例', 'number', NOW(), NOW()),
('commission_rate_level3', '0.01', '三级佣金比例', 'number', NOW(), NOW()),
('commission_enabled', 'true', '是否启用佣金', 'boolean', NOW(), NOW()),
('yuebao_annual_rate', '0.0365', '余额宝年化利率', 'number', NOW(), NOW()),
('yuebao_min_transfer', '1', '最低转入金额', 'number', NOW(), NOW()),
('yuebao_enabled', 'true', '是否启用余额宝', 'boolean', NOW(), NOW())
ON DUPLICATE KEY UPDATE `config_value` = VALUES(`config_value`);
