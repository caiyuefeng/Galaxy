package com.galaxy.saturn.core;

import com.galaxy.saturn.store.DataPool;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 消费者线程
 * @date : 2018/12/24 9:52
 **/
public class Writer implements Runnable {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(Writer.class);

    /**
     * 线程名
     */
    private String threadName;

    /**
     * 数据输出操作
     */
    private Output output;

    /**
     * 线程池实例
     */
    private DataPool dataPool = DataPool.getInstance();

    /**
     * 文件读取完毕标志
     */
    private boolean stop = false;

    /**
     * 线程停止标志
     */
    private boolean alreadyDie = false;

    public Writer(String threadName) {
        this.threadName = threadName;
        output = new MergeFileOutput();
    }

    public boolean isAlreadyDie() {
        return alreadyDie;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        // 设置当前线程名
        Thread.currentThread().setName(StringUtils.substringBeforeLast(Thread.currentThread().getName(), "_")
                + "_" + threadName);
        LOG.info("消费者线程:{} 开始执行...", threadName);
        while (!stop) {
            String value = dataPool.poll();
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            output.take(value);
        }
        alreadyDie = true;
        output.end();
        LOG.info("消费者线程:{} 执行完毕!", threadName);
    }


}
