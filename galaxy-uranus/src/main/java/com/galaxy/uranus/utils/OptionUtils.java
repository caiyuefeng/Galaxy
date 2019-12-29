package com.galaxy.uranus.utils;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:31 2019/12/23
 * @Modified By:
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
		if (val.startsWith("--")) {
			return val.substring(2);
		} else if (val.startsWith("-")) {
			return val.substring(1);
		}
		return val;
	}

	public static boolean isJavaProperty(String token) {
		return token.startsWith("D");
	}
}
