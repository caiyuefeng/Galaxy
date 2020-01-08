package com.galaxy.uranus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 可选参数值注解
 * 说明：
 * 1、该注解未OptionAnnotation注解的补充注解
 * 该注解用于标识OptionAnnotation的指定的参数项的参数值为可选参数值
 * 即在命令行输入参数时，若对应参数项未输入对应参数值也是可以正常解析的
 * 2、该参数项结束字符串类型的数组作为默认参数值，即当OptionAnnotation注解
 * 对应的参数项未从命令行接受到参数时，则将该注解保存的参数值填入参数项中
 * @Date : Create in 18:57 2020/1/8
 * @Modified By:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OptionalArgument {
	String[] value() default {};
}
