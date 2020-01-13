package com.galaxy.sun.base;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  数据新旧类型标志
 * @date 2018/12/11 20:20
 **/
public enum DataType {

    /**
     * 旧数据类型
     */
    OLD("O"),

    /**
     * 新数据类型
     */
    NEW("N");

    private String value;

    DataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
