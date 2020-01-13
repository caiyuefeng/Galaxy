package com.galaxy.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 15:18 2019/10/3
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Run {
}
