package com.galaxy.stone;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:46 2019/8/14
 * @Modified By:
 */
public enum Symbol {
    /**
     * 字符串标志
     */
    EMPTY_STR(""),
    DOT(".");

    String value;

    Symbol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
