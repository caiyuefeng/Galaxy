package com.galaxy.uranus.utils;

import com.galaxy.stone.Symbol;
import com.galaxy.uranus.annotation.AnnotationRegistration;
import com.galaxy.uranus.annotation.OptionAnnotation;
import com.galaxy.uranus.annotation.OptionalArgument;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 22:31 2019/12/23
 *
 */
public final class OptionUtils {

	private OptionUtils() {
		throw new UnsupportedOperationException("unable instantiation class");
	}

	/**
	 * 格式化参数值
	 * 参数值不能以
	 * -- - 开头
	 *
	 * @param val 参数值
	 * @return 格式化后参数值
	 */
	public static String formatOptionVal(String val) {
		if (val.startsWith(Symbol.DOUBLE_SHORT_RUNG.getValue())) {
			return val.substring(2);
		} else if (val.startsWith(Symbol.SHORT_RUNG.getValue())) {
			return val.substring(1);
		}
		return val;
	}

	public static boolean isJavaProperty(String token) {
		return token.startsWith("D");
	}

	public static Map<String, List<String>> getPropertiesFromOptionAnnotation() throws IOException, ClassNotFoundException {
		Map<String, List<String>> properties = new HashMap<>();
		AnnotationRegistration registration = AnnotationRegistration.getInstance();
		Iterator<Map.Entry<Annotation, Class<?>>> iterator = registration.iterator(annotation -> annotation instanceof OptionalArgument);
		while (iterator.hasNext()) {
			Map.Entry<Annotation, Class<?>> entry = iterator.next();
			OptionalArgument argument = (OptionalArgument) entry.getKey();
			OptionAnnotation annotation = entry.getValue().getAnnotation(OptionAnnotation.class);
			if (annotation != null) {
				properties.put(annotation.opt(), Arrays.asList(argument.value()));
			}
		}
		return properties;
	}
}
