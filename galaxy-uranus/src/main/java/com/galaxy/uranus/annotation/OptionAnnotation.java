package com.galaxy.uranus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Option注解类。
 * <p>
 * 使用说明:
 * 该注解类用于标识在功能模块类上，和命令行参数进行绑定
 * 参数解析在命令行参数时，会先进行命令参数模块绑定，
 * 在用户代码根据输入的命令行参数项直接获取到对应的功能模块实例。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 13:05 2019/12/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OptionAnnotation {
	/**
	 * @return 短参名
	 */
	String opt();

	/**
	 * @return 长参名
	 */
	String longOpt() default "";

	/**
	 * @return 参数项是否必须输入
	 */
	boolean isRequired() default false;

	/**
	 * @return 绑定的参数值
	 */
	String bindValue() default "";

	/**
	 * @return 参数值分隔符
	 */
	char valueSeq() default ' ';

	/**
	 * @return 是否接受参数
	 */
	boolean hasArgs() default false;

	/**
	 * @return 接受的参数值个数
	 */
	int numOfArgs() default -1;

	/**
	 * @return 参数项描述
	 */
	String desc() default "";

	/**
	 * @return 参数项组名称
	 */
	String groupName() default "default";
}
