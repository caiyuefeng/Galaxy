package com.galaxy.sirius.annotation;

import com.galaxy.sirius.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 21:36 2019/8/5
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sync {

    int num() default 1;

    Role role() default Role.COMMON;
}
