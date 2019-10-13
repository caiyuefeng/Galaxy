package com.galaxy.saturn.store;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 数据池接口
 * @Date : Create in 13:47 2019/10/5
 * @Modified By:
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
