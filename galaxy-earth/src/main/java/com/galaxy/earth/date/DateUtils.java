package com.galaxy.earth.date;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  日期工具类
 * @date 2018/12/10 14:07
 **/
public class DateUtils {
    /**
     * 日期对象实例
     */
    public static final Date DATE = new Date();

    /**
     * 日期格式化对象实例
     */
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat();

    /**
     * 当前时间
     */
    private static volatile String CURRENT_TIME = "";

    /**
     * 从绝对秒中获取 yyyyMMddHHmmss日期
     *
     * @param time 绝对秒
     * @return 字符串格式的日期
     */
    public static synchronized String getTime(long time) {
        TIME_FORMAT.applyPattern("yyyyMMddHHmmss");
        DATE.setTime(time * 1000);
        return TIME_FORMAT.format(DATE);
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期 格式 yyyyMMdd
     */
    public static String getDate() {
        // TODO 待测试多线程环境
        if ("".equals(CURRENT_TIME)) {
            synchronized (DateUtils.class) {
                if ("".equals(CURRENT_TIME)) {
                    DATE.setTime(System.currentTimeMillis());
                    TIME_FORMAT.applyPattern("yyyyMMdd");
                    CURRENT_TIME = TIME_FORMAT.format(DATE);
                }
            }
        }
        return CURRENT_TIME;
    }
}
