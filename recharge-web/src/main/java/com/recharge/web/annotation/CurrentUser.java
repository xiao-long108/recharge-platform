package com.recharge.web.annotation;

import java.lang.annotation.*;

/**
 * 获取当前登录用户ID的注解
 * 用于Controller方法参数上，自动注入当前登录用户的ID
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
