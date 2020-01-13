package com.galaxy.uranus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可选参数值注解。
 * <p>
 * 该注解功能如下：
 * <ul>
 *     <li>该注解为{@link OptionAnnotation}注解的补充注解:
 *     该注解用于标识{@link OptionAnnotation}的指定的参数项的参数值为可选参数值
 *     即在命令行输入参数时，若对应参数项未输入对应参数值也是可以正常解析的
 *     </li>
 *     <li>该参数项结束字符串类型的数组作为默认参数值，即当{@link OptionAnnotation}注解对应的参数项未从命令行接受到参数时，
 *     则将该注解保存的参数值填入参数项中</li>
 * </ul>
 * <p>
 * 该注解接受字符串数组，默认情况下空为字符串数组。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 18:57 2020/1/8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OptionalArgument {
	String[] value() default {};
}
