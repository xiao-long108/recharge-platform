package com.recharge.common.constant;

/**
 * Redis Key常量
 */
public interface RedisKeyConstant {

    /**
     * 用户Token前缀
     */
    String USER_TOKEN_PREFIX = "user:token:";

    /**
     * 验证码前缀
     */
    String VERIFY_CODE_PREFIX = "verify:code:";

    /**
     * 用户信息缓存前缀
     */
    String USER_INFO_PREFIX = "user:info:";

    /**
     * 系统配置缓存前缀
     */
    String SYSTEM_CONFIG_PREFIX = "system:config:";

    /**
     * 产品列表缓存
     */
    String PRODUCT_LIST = "product:list";

    /**
     * 店铺信息缓存前缀
     */
    String STORE_INFO_PREFIX = "store:info:";

    /**
     * Token过期时间（秒）- 7天
     */
    long TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    /**
     * 验证码过期时间（秒）- 5分钟
     */
    long VERIFY_CODE_EXPIRE_SECONDS = 5 * 60;

    /**
     * 用户信息缓存过期时间（秒）- 1小时
     */
    long USER_INFO_EXPIRE_SECONDS = 60 * 60;
}
