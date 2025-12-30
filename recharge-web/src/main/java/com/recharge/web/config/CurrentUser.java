package com.recharge.web.config;

import java.lang.annotation.*;

/**
 * 获取当前登录用户ID注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

    /**
     * 是否必须登录，默认true
     * 设置为false时，未登录返回null而不抛异常
     */
    boolean required() default true;
}
