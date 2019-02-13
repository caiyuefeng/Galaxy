package com.galaxy.saturn.store;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/24 9:54
 **/
public class SaturnConfiguration {

    /**
     * 本地机器IP
     */
    public static String LOCAL_IP = "";

    /**
     * Zookeeper机器节点IP
     */
    public static String ZK_MACHINE_IP = "";

    /**
     * 数据缓存池最大缓存量
     */
    public static int MAX_SIZE = 0;

    /**
     * 读取线程数
     */
    public static int READER_NUM = 0;

    /**
     * 写线程数
     */
    public static int WRITER_NUM = 0;

    /**
     * 载入配置
     */
    public static void load() {
    }
}
