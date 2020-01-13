package com.galaxy.sun.partitioner;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  分区器接口
 * @date 2018/12/11 10:14
 **/
public interface DataPartitioner<Type> {

    /**
     * 生成分区
     *
     * @param o 入参
     * @return 分区信息
     */
    String encode(Type o);

    /**
     * 解码分区
     *
     * @param o 入参
     * @return 分区信息
     */
    String[] decode(Type o);
}
