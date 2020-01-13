package com.galaxy.uranus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数绑定类型注解。
 * <p>功能说明：</p>
 * 该Option注解类用于标识绑定的功能模块时参数名绑定还是参数值绑定,
 * 参数绑定时分为两种绑定方式
 * <ul>
 *     <li>参数项不接受参数值时，只能通过参数名进行绑定</li>
 *     <li>参数项接受参数值时，功能模块可以通过参数名进行绑定也可以通过参数值进行绑定,
 *     此注解就是指定功能模块采用哪种方式进行绑定</li>
 * </ul>
 * <p>
 * 接受参数值为 {@link OptionBindTypeEnum} 枚举类
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 13:10 2019/12/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OptionBindType {
	OptionBindTypeEnum value() default OptionBindTypeEnum.TYPE_BIND;
}
