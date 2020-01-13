package com.galaxy.saturn.store;

/**
 * @author 蔡月峰
 * @version 1.0
 *  数据池接口
 * @date Create in 13:47 2019/10/5
 *
 */
public interface DataPool {

    /**
     * 推进操作
     */
    void put(String value);

    /**
     * 拉取操作
     *
     * @return 值
     */
    String poll();

    /**
     * 关闭数据池
     *
     * @throws Exception 异常信息
     */
    void close() throws Exception;

    boolean isClose();
}
