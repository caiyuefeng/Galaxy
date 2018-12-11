package com.galaxy.base;

import java.io.UnsupportedEncodingException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 公用字符类
 * @date : 2018/12/10 14:41
 **/
public class ConstantChar {

    public static final String UTF8 = "UTF-8";

    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";

    /**
     * 斜杠符号
     */
    public static final String SLASH = "/";

    public static final byte[] NEW_LINE;

    static {
        try {
            NEW_LINE = "\n".getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("cna not find UTF-8 encode!");
        }
    }
}
