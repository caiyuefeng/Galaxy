package com.galaxy.sirius.classloader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 基础类加载器接口。
 * 该接口用于缓存启动类加载器加载的所有类对象、字节码和类文件路径。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 18:23 2020/1/23
 */
public interface BaseClassLoader {

	/**
	 * 类缓存
	 */
	Map<String, Class<?>> CLASS_BUFFER = new HashMap<>();

	/**
	 * 类文件路径缓存
	 */
	Map<String, String> CLASS_PATH_BUFFER = new HashMap<>();

	/**
	 * 类名=>类字节码。
	 */
	Map<String, byte[]> CLASS_BYTE_BUFFER = new HashMap<>();

	/**
	 * 获取第一个符合lambda表达式的类。
	 *
	 * @param predicate lambda表达式
	 * @return 类签名。
	 */
	static Class<?> getFirstClass(Predicate<Class<?>> predicate) {
		return CLASS_BUFFER.values().stream().filter(predicate).findFirst().orElse(null);
	}

	/**
	 * 获取所有符合lambda表达式的类签名集合。
	 *
	 * @param predicate lambda表达式
	 * @return 类集合
	 */
	static List<Class<?>> getClass(Predicate<Class<?>> predicate) {
		return CLASS_BUFFER.values().stream().filter(predicate).collect(Collectors.toList());
	}
}
