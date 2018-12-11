package com.galaxy.base;

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
