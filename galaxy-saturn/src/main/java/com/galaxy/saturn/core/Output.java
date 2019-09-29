package com.galaxy.saturn.core;

public interface Output {

    /**
     * 处理获取的数据
     *
     * @param line 获取的数据
     */
    void take(String line);

    /**
     * 结束操作
     */
    void end();
}
