package com.galaxy.sirius.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:36 2019/8/5
 * @Modified By:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Stage {
    int num() default 1;
}
