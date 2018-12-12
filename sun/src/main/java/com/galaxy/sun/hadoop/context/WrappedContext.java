package com.galaxy.sun.hadoop.context;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Counter;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 自定义Context上下文包装类型
 * @date : 2018/12/11 9:37
 **/
public interface WrappedContext {

    /**
     * context输出方法接口
     * 用于输出二次排序的数据
     *
     * @param key      K
     * @param value    V
     * @param sortSeed 排序
     * @throws IOException          1
     * @throws InterruptedException 2
     */
    void write(String key, String value, String sortSeed) throws IOException, InterruptedException;

    /**
     * context通用的输出方法接口
     *
     * @param key   K
     * @param value V
     * @throws IOException          1
     * @throws InterruptedException 2
     */
    void write(String key, String value) throws IOException, InterruptedException;

    /**
     * context 输出方法接口
     * 通常用于输出分区任务数据
     *
     * @param key K
     * @throws IOException          1
     * @throws InterruptedException 2
     */
    void write(String key) throws IOException, InterruptedException;

    /**
     * 获取当前上下文的统计量实例
     *
     * @param group 统计量组
     * @param item  统计量项
     * @return 统计量实例
     */
    Counter getCounter(String group, String item);

    /**
     * 设置当前默认分区
     *
     * @param part 分区信息
     */
    void setDefaultPart(String part);

    /**
     * 获取当前分区
     *
     * @return 分区信息
     */
    String getDefaultPart();

    /**
     * 获取任务配置信息
     *
     * @return 任务配置信息
     */
    Configuration getConfiguration();
}
