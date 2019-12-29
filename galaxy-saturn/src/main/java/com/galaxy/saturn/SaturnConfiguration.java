package com.galaxy.saturn;

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
    public static String LOCAL_IP = "127.0.0.1";

    /**
     * Zookeeper机器节点IP
     */
    public static String ZK_MACHINE_IP = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";

    /**
     * 数据缓存池最大缓存量
     */
    public static int MAX_SIZE = 50;

    /**
     * 读取线程数
     */
    static int READER_NUM = 2;

    /**
     * 写线程数
     */
    static int WRITER_NUM = 1;

    /**
     * 载入配置
     */
    public static void load() {
    }
}
