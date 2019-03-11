package com.galaxy.meteorolite.string;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 字符串工具类
 * @date : 2018/12/10 14:08
 **/
public class GalaxyStringUtils {
    public static String max(String first, String second) {
        if (isEmpty(first)) {
            return second;
        }
        if (isEmpty(second)) {
            return first;
        }
        return first.compareTo(second) < 0 ? second : first;
    }

    public static boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }
}
