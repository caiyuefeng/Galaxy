package com.galaxy.earth;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  字符串工具类
 * @date 2018/12/10 14:08
 **/
public class StringUtils {

    private StringUtils() {
        throw new RuntimeException("can not create instance");
    }


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
