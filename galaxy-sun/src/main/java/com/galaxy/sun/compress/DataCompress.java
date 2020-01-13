package com.galaxy.sun.compress;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  数据压缩方法接口
 * @date 2018/12/11 10:17
 **/
public interface DataCompress {

    /**
     * 压缩数据
     *
     * @param data 未压缩数据
     * @return 压缩后的数据
     */
    String compress(String data);

    /**
     * 解压缩数据
     *
     * @param data 已压缩数据
     * @return 解压后数据
     */
    String decompress(String data);
}
