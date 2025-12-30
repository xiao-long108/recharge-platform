-- ============================================================
-- 话费充值系统扩展模块 DDL
-- 签到、优惠券、分销、店铺商品、帮助中心、轮播图等
-- ============================================================

USE recharge_platform;

-- ============================================================
-- 1. 签到记录表 (t_sign_record)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_sign_record (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '签到记录ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    sign_date           DATE                NOT NULL COMMENT '签到日期',
    continuous_days     INT UNSIGNED        NOT NULL DEFAULT 1 COMMENT '连续签到天数',
    reward_type         TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '奖励类型: 1-积分 2-余额 3-优惠券',
    reward_amount       DECIMAL(10,2)       NOT NULL DEFAULT 0.00 COMMENT '奖励金额/积分',
    reward_coupon_id    BIGINT UNSIGNED     NULL COMMENT '奖励优惠券ID',
    remark              VARCHAR(255)        NULL COMMENT '备注',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_date (user_id, sign_date),
    KEY idx_user_id (user_id),
    KEY idx_sign_date (sign_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到记录表';

-- ============================================================
-- 2. 优惠券模板表 (t_coupon)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_coupon (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    name                VARCHAR(100)        NOT NULL COMMENT '优惠券名称',
    coupon_type         TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '类型: 1-满减券 2-折扣券 3-无门槛券',
    discount_amount     DECIMAL(10,2)       NOT NULL DEFAULT 0.00 COMMENT '优惠金额（满减/无门槛）',
    discount_rate       DECIMAL(5,4)        NULL COMMENT '折扣比例（折扣券）',
    min_amount          DECIMAL(10,2)       NOT NULL DEFAULT 0.00 COMMENT '使用门槛金额',
    max_discount        DECIMAL(10,2)       NULL COMMENT '最高优惠金额（折扣券）',
    total_count         INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '发放总量（0表示不限）',
    used_count          INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '已领取数量',
    per_user_limit      INT UNSIGNED        NOT NULL DEFAULT 1 COMMENT '每人限领数量',
    valid_days          INT UNSIGNED        NULL COMMENT '有效天数（领取后）',
    start_time          DATETIME            NULL COMMENT '固定有效期开始',
    end_time            DATETIME            NULL COMMENT '固定有效期结束',
    use_scope           TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '使用范围: 0-全部 1-指定产品 2-指定分类',
    scope_ids           VARCHAR(1000)       NULL COMMENT '范围ID列表（JSON数组）',
    description         VARCHAR(500)        NULL COMMENT '使用说明',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_coupon_type (coupon_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券模板表';

-- ============================================================
-- 3. 用户优惠券表 (t_user_coupon)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_user_coupon (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    coupon_id           BIGINT UNSIGNED     NOT NULL COMMENT '优惠券模板ID',
    coupon_name         VARCHAR(100)        NOT NULL COMMENT '优惠券名称（冗余）',
    coupon_type         TINYINT UNSIGNED    NOT NULL COMMENT '优惠券类型（冗余）',
    discount_amount     DECIMAL(10,2)       NOT NULL DEFAULT 0.00 COMMENT '优惠金额（冗余）',
    discount_rate       DECIMAL(5,4)        NULL COMMENT '折扣比例（冗余）',
    min_amount          DECIMAL(10,2)       NOT NULL DEFAULT 0.00 COMMENT '使用门槛（冗余）',
    valid_start_time    DATETIME            NOT NULL COMMENT '有效开始时间',
    valid_end_time      DATETIME            NOT NULL COMMENT '有效结束时间',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '状态: 0-未使用 1-已使用 2-已过期',
    used_time           DATETIME            NULL COMMENT '使用时间',
    used_order_id       BIGINT UNSIGNED     NULL COMMENT '使用订单ID',
    source              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '来源: 1-领取 2-签到 3-活动 4-系统发放',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_coupon_id (coupon_id),
    KEY idx_status (status),
    KEY idx_valid_end_time (valid_end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券表';

-- ============================================================
-- 4. 店铺商品表 (t_store_product)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_store_product (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '店铺商品ID',
    store_id            BIGINT UNSIGNED     NOT NULL COMMENT '店铺ID',
    product_id          BIGINT UNSIGNED     NULL COMMENT '关联系统产品ID',
    name                VARCHAR(200)        NOT NULL COMMENT '商品名称',
    description         VARCHAR(1000)       NULL COMMENT '商品描述',
    image               VARCHAR(500)        NULL COMMENT '商品图片',
    face_value          DECIMAL(10,2)       NOT NULL COMMENT '面值',
    original_price      DECIMAL(10,2)       NOT NULL COMMENT '原价',
    price               DECIMAL(10,2)       NOT NULL COMMENT '售价',
    stock               INT                 NOT NULL DEFAULT -1 COMMENT '库存（-1表示无限）',
    sales               INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '销量',
    sort_order          INT                 NOT NULL DEFAULT 0 COMMENT '排序',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_store_id (store_id),
    KEY idx_product_id (product_id),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺商品表';

-- ============================================================
-- 5. 分销等级表 (t_distribution_level)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_distribution_level (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '分销等级ID',
    name                VARCHAR(50)         NOT NULL COMMENT '等级名称',
    level               INT UNSIGNED        NOT NULL COMMENT '等级值',
    icon                VARCHAR(500)        NULL COMMENT '等级图标',
    price               DECIMAL(10,2)       NOT NULL DEFAULT 0.00 COMMENT '升级价格',
    commission_rate_1   DECIMAL(5,4)        NOT NULL DEFAULT 0.0000 COMMENT '一级佣金比例',
    commission_rate_2   DECIMAL(5,4)        NOT NULL DEFAULT 0.0000 COMMENT '二级佣金比例',
    commission_rate_3   DECIMAL(5,4)        NOT NULL DEFAULT 0.0000 COMMENT '三级佣金比例',
    privileges          TEXT                NULL COMMENT '等级特权说明（JSON）',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    sort_order          INT                 NOT NULL DEFAULT 0 COMMENT '排序',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_level (level),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分销等级表';

-- ============================================================
-- 6. 用户分销等级记录表 (t_user_distribution)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_user_distribution (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '用户分销ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    level_id            BIGINT UNSIGNED     NOT NULL COMMENT '分销等级ID',
    level               INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '当前等级值',
    upgrade_time        DATETIME            NULL COMMENT '升级时间',
    expire_time         DATETIME            NULL COMMENT '过期时间',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-过期 1-有效',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_level_id (level_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户分销等级记录表';

-- ============================================================
-- 7. 帮助文章表 (t_help_article)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_help_article (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    category_id         BIGINT UNSIGNED     NULL COMMENT '分类ID',
    title               VARCHAR(200)        NOT NULL COMMENT '文章标题',
    content             TEXT                NOT NULL COMMENT '文章内容',
    view_count          INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '浏览次数',
    sort_order          INT                 NOT NULL DEFAULT 0 COMMENT '排序',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-隐藏 1-显示',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮助文章表';

-- ============================================================
-- 8. 帮助分类表 (t_help_category)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_help_category (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    name                VARCHAR(50)         NOT NULL COMMENT '分类名称',
    icon                VARCHAR(500)        NULL COMMENT '分类图标',
    sort_order          INT                 NOT NULL DEFAULT 0 COMMENT '排序',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮助分类表';

-- ============================================================
-- 9. 轮播图表 (t_banner)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_banner (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
    title               VARCHAR(100)        NOT NULL COMMENT '标题',
    image               VARCHAR(500)        NOT NULL COMMENT '图片URL',
    link_type           TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '链接类型: 0-无 1-内部页面 2-外部链接 3-产品详情',
    link_url            VARCHAR(500)        NULL COMMENT '链接地址',
    link_id             BIGINT UNSIGNED     NULL COMMENT '关联ID（产品ID等）',
    position            VARCHAR(50)         NOT NULL DEFAULT 'home' COMMENT '展示位置: home-首页 store-店铺',
    sort_order          INT                 NOT NULL DEFAULT 0 COMMENT '排序',
    start_time          DATETIME            NULL COMMENT '开始展示时间',
    end_time            DATETIME            NULL COMMENT '结束展示时间',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_position (position),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- ============================================================
-- 10. 公告表 (t_notice)
-- ============================================================
CREATE TABLE IF NOT EXISTS t_notice (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    title               VARCHAR(200)        NOT NULL COMMENT '公告标题',
    content             TEXT                NOT NULL COMMENT '公告内容',
    notice_type         TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '公告类型: 1-系统公告 2-活动通知 3-更新日志',
    is_top              TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '是否置顶: 0-否 1-是',
    view_count          INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '浏览次数',
    start_time          DATETIME            NULL COMMENT '开始展示时间',
    end_time            DATETIME            NULL COMMENT '结束展示时间',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_notice_type (notice_type),
    KEY idx_status (status),
    KEY idx_is_top (is_top)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ============================================================
-- 初始化扩展模块数据
-- ============================================================

-- 签到配置
INSERT INTO t_system_config (config_key, config_value, config_desc, config_type) VALUES
('sign_reward_day1', '0.1', '签到第1天奖励金额', 'number'),
('sign_reward_day2', '0.2', '签到第2天奖励金额', 'number'),
('sign_reward_day3', '0.3', '签到第3天奖励金额', 'number'),
('sign_reward_day4', '0.5', '签到第4天奖励金额', 'number'),
('sign_reward_day5', '0.8', '签到第5天奖励金额', 'number'),
('sign_reward_day6', '1.0', '签到第6天奖励金额', 'number'),
('sign_reward_day7', '2.0', '签到第7天奖励金额', 'number'),
('sign_continuous_bonus', '5.0', '连续7天额外奖励', 'number')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);

-- 优惠券模板数据
INSERT INTO t_coupon (name, coupon_type, discount_amount, min_amount, total_count, per_user_limit, valid_days, description, status) VALUES
('新人专享券', 1, 5.00, 50.00, 0, 1, 30, '新用户专享，满50减5', 1),
('满100减10', 1, 10.00, 100.00, 1000, 3, 7, '满100元可用，减10元', 1),
('满200减25', 1, 25.00, 200.00, 500, 2, 7, '满200元可用，减25元', 1),
('签到礼券', 1, 2.00, 20.00, 0, 99, 3, '签到专属优惠券', 1);

-- 分销等级数据
INSERT INTO t_distribution_level (name, level, price, commission_rate_1, commission_rate_2, commission_rate_3, privileges, sort_order, status) VALUES
('普通会员', 0, 0.00, 0.0300, 0.0200, 0.0100, '["基础佣金比例"]', 0, 1),
('铜牌会员', 1, 99.00, 0.0500, 0.0300, 0.0150, '["提升佣金比例","专属客服"]', 1, 1),
('银牌会员', 2, 299.00, 0.0800, 0.0500, 0.0200, '["高额佣金比例","专属客服","优先提现"]', 2, 1),
('金牌会员', 3, 599.00, 0.1000, 0.0600, 0.0300, '["顶级佣金比例","专属客服","优先提现","专属活动"]', 3, 1);

-- 帮助分类数据
INSERT INTO t_help_category (name, icon, sort_order, status) VALUES
('新手指南', NULL, 1, 1),
('充值问题', NULL, 2, 1),
('提现问题', NULL, 3, 1),
('账户安全', NULL, 4, 1),
('团队佣金', NULL, 5, 1);

-- 帮助文章数据
INSERT INTO t_help_article (category_id, title, content, sort_order, status) VALUES
(1, '如何注册账号？', '<p>1. 点击首页"注册"按钮</p><p>2. 输入手机号获取验证码</p><p>3. 设置登录密码完成注册</p>', 1, 1),
(1, '如何进行话费充值？', '<p>1. 选择充值金额</p><p>2. 输入充值手机号</p><p>3. 确认支付完成充值</p>', 2, 1),
(2, '充值多久到账？', '<p>正常情况下0-48小时内到账，高峰期可能略有延迟，请耐心等待。</p>', 1, 1),
(2, '充值失败怎么办？', '<p>充值失败后金额会自动退回到账户余额，请查看账户明细。</p>', 2, 1),
(3, '提现需要多久到账？', '<p>提现申请提交后，一般1-3个工作日内到账。</p>', 1, 1),
(3, '提现手续费是多少？', '<p>提现手续费为0.6%，最低100元起提。</p>', 2, 1),
(4, '如何修改登录密码？', '<p>进入"我的"-"设置"-"修改登录密码"，验证原密码后设置新密码。</p>', 1, 1),
(4, '如何设置支付密码？', '<p>进入"我的"-"设置"-"设置支付密码"，首次设置无需验证。</p>', 2, 1),
(5, '如何获得佣金？', '<p>邀请好友注册并完成充值，您将获得相应比例的佣金奖励。</p>', 1, 1),
(5, '佣金如何提现？', '<p>佣金可在"我的"-"提现"中申请提现到绑定的银行卡。</p>', 2, 1);

-- 轮播图数据
INSERT INTO t_banner (title, image, link_type, link_url, position, sort_order, status) VALUES
('新人专享优惠', '/static/banner/banner1.jpg', 1, '/pages/coupons/index', 'home', 1, 1),
('话费充值低至9.5折', '/static/banner/banner2.jpg', 0, NULL, 'home', 2, 1),
('邀请好友得佣金', '/static/banner/banner3.jpg', 1, '/pages/invite/index', 'home', 3, 1);

-- 公告数据
INSERT INTO t_notice (title, content, notice_type, is_top, status) VALUES
('欢迎使用话费充值平台', '<p>欢迎使用我们的话费充值服务，充值低至9.5折，更有签到红包、邀请佣金等福利等您来领！</p>', 1, 1, 1),
('充值到账时间说明', '<p>由于运营商系统原因，充值到账时间为0-48小时，请耐心等待。</p>', 1, 0, 1);
