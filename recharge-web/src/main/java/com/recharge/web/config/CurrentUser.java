package com.recharge.web.config;

import java.lang.annotation.*;

/**
 * 获取当前登录用户ID注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
