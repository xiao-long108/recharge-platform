-- ============================================================
-- 话费充值系统数据库 DDL
-- 数据库: recharge_platform
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS recharge_platform
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE recharge_platform;

-- ============================================================
-- 1. 用户表 (t_user)
-- ============================================================
CREATE TABLE t_user (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    phone               VARCHAR(20)         NOT NULL COMMENT '手机号（登录账号）',
    password            VARCHAR(255)        NOT NULL COMMENT '密码（BCrypt加密）',
    pay_password        VARCHAR(255)        NULL COMMENT '支付密码',
    nickname            VARCHAR(50)         NULL COMMENT '昵称',
    avatar              VARCHAR(500)        NULL COMMENT '头像URL',
    balance             DECIMAL(12,2)       NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    commission_balance  DECIMAL(12,2)       NOT NULL DEFAULT 0.00 COMMENT '佣金余额',
    frozen_balance      DECIMAL(12,2)       NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    auth_status         TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '认证状态: 0-未认证 1-认证中 2-已认证 3-认证失败',
    real_name           VARCHAR(50)         NULL COMMENT '真实姓名',
    id_card             VARCHAR(18)         NULL COMMENT '身份证号',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '账户状态: 0-禁用 1-正常',
    invite_code         VARCHAR(20)         NULL COMMENT '邀请码',
    parent_id           BIGINT UNSIGNED     NULL COMMENT '上级用户ID',
    level               TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '用户等级',
    last_login_time     DATETIME            NULL COMMENT '最后登录时间',
    last_login_ip       VARCHAR(45)         NULL COMMENT '最后登录IP',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_invite_code (invite_code),
    KEY idx_parent_id (parent_id),
    KEY idx_status (status),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 店铺表 (t_store)
-- ============================================================
CREATE TABLE t_store (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
    store_name          VARCHAR(100)        NOT NULL COMMENT '店铺名称',
    store_logo          VARCHAR(500)        NULL COMMENT '店铺Logo',
    store_desc          TEXT                NULL COMMENT '店铺描述',
    store_balance       DECIMAL(12,2)       NOT NULL DEFAULT 0.00 COMMENT '店铺余额',
    yuebao_balance      DECIMAL(12,2)       NOT NULL DEFAULT 0.00 COMMENT '余额宝余额',
    total_sales         INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '总销量',
    total_amount        DECIMAL(14,2)       NOT NULL DEFAULT 0.00 COMMENT '总销售额',
    today_amount        DECIMAL(12,2)       NOT NULL DEFAULT 0.00 COMMENT '今日销售额',
    today_orders        INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '今日订单数',
    level               TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '店铺等级',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-关闭 1-营业 2-审核中',
    owner_id            BIGINT UNSIGNED     NOT NULL COMMENT '店主用户ID',
    commission_rate     DECIMAL(5,4)        NOT NULL DEFAULT 0.0000 COMMENT '佣金比例',
    open_time           DATETIME            NULL COMMENT '开店时间',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_owner_id (owner_id),
    KEY idx_status (status),
    KEY idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺表';

-- ============================================================
-- 3. 产品表 (t_product)
-- ============================================================
CREATE TABLE t_product (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '产品ID',
    title               VARCHAR(200)        NOT NULL COMMENT '产品名称',
    description         VARCHAR(1000)       NULL COMMENT '产品描述',
    face_value          DECIMAL(10,2)       NOT NULL COMMENT '面值',
    price               DECIMAL(10,2)       NOT NULL COMMENT '售价',
    cost_price          DECIMAL(10,2)       NULL COMMENT '成本价',
    discount_tag        VARCHAR(50)         NULL COMMENT '折扣标签',
    operator_type       TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '运营商: 0-全部 1-移动 2-联通 3-电信',
    product_type        TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '产品类型: 1-话费 2-流量',
    sort_order          INT                 NOT NULL DEFAULT 0 COMMENT '排序',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    sales               INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '销量',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order),
    KEY idx_operator_type (operator_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

-- ============================================================
-- 4. 订单表 (t_order)
-- ============================================================
CREATE TABLE t_order (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no            VARCHAR(32)         NOT NULL COMMENT '订单号',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    store_id            BIGINT UNSIGNED     NULL COMMENT '店铺ID',
    product_id          BIGINT UNSIGNED     NULL COMMENT '产品ID',
    mobile              VARCHAR(20)         NOT NULL COMMENT '充值手机号',
    face_value          DECIMAL(10,2)       NOT NULL COMMENT '面值',
    price               DECIMAL(10,2)       NOT NULL COMMENT '原价',
    pay_amount          DECIMAL(10,2)       NOT NULL COMMENT '实付金额',
    coupon_amount       DECIMAL(10,2)       NOT NULL DEFAULT 0.00 COMMENT '优惠券抵扣',
    coupon_id           BIGINT UNSIGNED     NULL COMMENT '优惠券ID',
    order_status        TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '订单状态: 0-待支付 1-支付中 2-充值中 3-充值成功 4-充值失败 5-已取消 6-已退款',
    pay_status          TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '支付状态: 0-未支付 1-支付中 2-已支付 3-退款中 4-已退款',
    pay_type            TINYINT UNSIGNED    NULL COMMENT '支付方式: 1-余额 2-支付宝 3-微信',
    pay_time            DATETIME            NULL COMMENT '支付时间',
    recharge_time       DATETIME            NULL COMMENT '充值完成时间',
    third_order_no      VARCHAR(64)         NULL COMMENT '第三方订单号',
    remark              VARCHAR(500)        NULL COMMENT '备注',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_store_id (store_id),
    KEY idx_order_status (order_status),
    KEY idx_pay_status (pay_status),
    KEY idx_mobile (mobile),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================================
-- 5. 银行账户表 (t_bank_account)
-- ============================================================
CREATE TABLE t_bank_account (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '银行账户ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    bank_name           VARCHAR(100)        NOT NULL COMMENT '银行名称',
    bank_code           VARCHAR(20)         NULL COMMENT '银行编码',
    account_holder      VARCHAR(50)         NOT NULL COMMENT '持卡人姓名',
    account_number      VARCHAR(30)         NOT NULL COMMENT '银行卡号',
    id_card             VARCHAR(18)         NULL COMMENT '身份证号',
    branch_name         VARCHAR(200)        NULL COMMENT '开户支行',
    is_default          TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '是否默认: 0-否 1-是',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='银行账户表';

-- ============================================================
-- 6. 余额宝账户表 (t_yuebao_account)
-- ============================================================
CREATE TABLE t_yuebao_account (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '余额宝账户ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    balance             DECIMAL(14,2)       NOT NULL DEFAULT 0.00 COMMENT '余额宝余额',
    total_income        DECIMAL(14,4)       NOT NULL DEFAULT 0.0000 COMMENT '累计收益',
    yesterday_income    DECIMAL(10,4)       NOT NULL DEFAULT 0.0000 COMMENT '昨日收益',
    annual_rate         DECIMAL(6,4)        NOT NULL DEFAULT 0.0365 COMMENT '当前年化利率',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '状态: 0-冻结 1-正常',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='余额宝账户表';

-- ============================================================
-- 7. 余额宝交易记录表 (t_yuebao_record)
-- ============================================================
CREATE TABLE t_yuebao_record (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    record_type         TINYINT UNSIGNED    NOT NULL COMMENT '类型: 1-转入 2-转出 3-收益',
    amount              DECIMAL(12,4)       NOT NULL COMMENT '金额',
    balance_before      DECIMAL(14,2)       NOT NULL COMMENT '变动前余额',
    balance_after       DECIMAL(14,2)       NOT NULL COMMENT '变动后余额',
    remark              VARCHAR(255)        NULL COMMENT '备注',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_record_type (record_type),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='余额宝交易记录表';

-- ============================================================
-- 8. 团队关系表 (t_team_relation)
-- ============================================================
CREATE TABLE t_team_relation (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    parent_id           BIGINT UNSIGNED     NOT NULL COMMENT '直属上级ID',
    ancestor_path       VARCHAR(500)        NULL COMMENT '祖先路径（如：,1,5,10,）',
    level               TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '层级深度',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_parent_id (parent_id),
    KEY idx_ancestor_path (ancestor_path(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队关系表';

-- ============================================================
-- 9. 佣金记录表 (t_commission_record)
-- ============================================================
CREATE TABLE t_commission_record (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '佣金记录ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '获得佣金的用户ID',
    from_user_id        BIGINT UNSIGNED     NOT NULL COMMENT '来源用户ID',
    order_id            BIGINT UNSIGNED     NOT NULL COMMENT '关联订单ID',
    order_amount        DECIMAL(10,2)       NOT NULL COMMENT '订单金额',
    commission_rate     DECIMAL(5,4)        NOT NULL COMMENT '佣金比例',
    commission_amount   DECIMAL(10,4)       NOT NULL COMMENT '佣金金额',
    commission_level    TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '佣金层级: 1-一级 2-二级 3-三级',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '状态: 0-待结算 1-已结算 2-已取消',
    settle_time         DATETIME            NULL COMMENT '结算时间',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_from_user_id (from_user_id),
    KEY idx_order_id (order_id),
    KEY idx_status (status),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='佣金记录表';

-- ============================================================
-- 10. 提现记录表 (t_withdraw_record)
-- ============================================================
CREATE TABLE t_withdraw_record (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '提现记录ID',
    withdraw_no         VARCHAR(32)         NOT NULL COMMENT '提现单号',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    bank_account_id     BIGINT UNSIGNED     NOT NULL COMMENT '银行账户ID',
    amount              DECIMAL(10,2)       NOT NULL COMMENT '提现金额',
    fee                 DECIMAL(10,2)       NOT NULL DEFAULT 0.00 COMMENT '手续费',
    actual_amount       DECIMAL(10,2)       NOT NULL COMMENT '实际到账金额',
    withdraw_type       TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '提现类型: 1-余额 2-佣金',
    status              TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核 1-处理中 2-成功 3-失败 4-已取消',
    audit_user_id       BIGINT UNSIGNED     NULL COMMENT '审核人ID',
    audit_time          DATETIME            NULL COMMENT '审核时间',
    audit_remark        VARCHAR(500)        NULL COMMENT '审核备注',
    complete_time       DATETIME            NULL COMMENT '完成时间',
    fail_reason         VARCHAR(500)        NULL COMMENT '失败原因',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_withdraw_no (withdraw_no),
    KEY idx_user_id (user_id),
    KEY idx_bank_account_id (bank_account_id),
    KEY idx_status (status),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现记录表';

-- ============================================================
-- 11. 账户流水表 (t_account_flow)
-- ============================================================
CREATE TABLE t_account_flow (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    user_id             BIGINT UNSIGNED     NOT NULL COMMENT '用户ID',
    flow_type           TINYINT UNSIGNED    NOT NULL COMMENT '流水类型: 1-充值 2-消费 3-退款 4-提现 5-佣金 6-余额宝转入 7-余额宝转出',
    amount              DECIMAL(12,2)       NOT NULL COMMENT '变动金额',
    balance_before      DECIMAL(12,2)       NOT NULL COMMENT '变动前余额',
    balance_after       DECIMAL(12,2)       NOT NULL COMMENT '变动后余额',
    account_type        TINYINT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '账户类型: 1-余额 2-佣金',
    relation_id         BIGINT UNSIGNED     NULL COMMENT '关联业务ID',
    relation_type       VARCHAR(50)         NULL COMMENT '关联业务类型',
    remark              VARCHAR(500)        NULL COMMENT '备注',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_flow_type (flow_type),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户流水表';

-- ============================================================
-- 12. 系统配置表 (t_system_config)
-- ============================================================
CREATE TABLE t_system_config (
    id                  BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    config_key          VARCHAR(100)        NOT NULL COMMENT '配置键',
    config_value        TEXT                NOT NULL COMMENT '配置值',
    config_desc         VARCHAR(500)        NULL COMMENT '配置描述',
    config_type         VARCHAR(20)         NOT NULL DEFAULT 'string' COMMENT '值类型: string/number/json/boolean',
    created_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 系统配置
INSERT INTO t_system_config (config_key, config_value, config_desc, config_type) VALUES
('yuebao_annual_rate', '0.0365', '余额宝年化利率', 'number'),
('withdraw_fee_rate', '0.006', '提现手续费比例', 'number'),
('withdraw_min_amount', '100', '最低提现金额', 'number'),
('withdraw_max_amount', '50000', '单笔最高提现金额', 'number'),
('commission_rate_level1', '0.05', '一级佣金比例', 'number'),
('commission_rate_level2', '0.03', '二级佣金比例', 'number'),
('commission_rate_level3', '0.01', '三级佣金比例', 'number');

-- 初始化产品数据
INSERT INTO t_product (title, description, face_value, price, discount_tag, operator_type, product_type, sort_order, status) VALUES
('10元话费', '全国通用话费充值', 10.00, 9.80, '9.8折', 0, 1, 1, 1),
('20元话费', '全国通用话费充值', 20.00, 19.60, '9.8折', 0, 1, 2, 1),
('30元话费', '全国通用话费充值', 30.00, 29.40, '9.8折', 0, 1, 3, 1),
('50元话费', '全国通用话费充值', 50.00, 48.50, '9.7折', 0, 1, 4, 1),
('100元话费', '全国通用话费充值', 100.00, 96.50, '9.65折', 0, 1, 5, 1),
('200元话费', '全国通用话费充值', 200.00, 192.00, '9.6折', 0, 1, 6, 1),
('300元话费', '全国通用话费充值', 300.00, 286.50, '9.55折', 0, 1, 7, 1),
('500元话费', '全国通用话费充值', 500.00, 475.00, '9.5折', 0, 1, 8, 1);
