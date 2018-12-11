package com.galaxy.base;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 增量全量数据文件标志
 * @date : 2018/12/11 20:20
 **/
public enum FileNameType {

    /**
     * 全量
     */
    TOTAL("TOTAL"),
    /**
     * 增量
     */
    IMPORT("IMPORT");

    private String value;

    FileNameType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FileNameType{" + "value=" + value + "}";
    }
}
