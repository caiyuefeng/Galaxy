package com.galaxy.saturn.core;

import com.galaxy.saturn.IsCloseable;
import com.galaxy.saturn.store.DataPool;
import com.galaxy.sirius.annotation.Sync;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 文件输出器
 * @date : 2018/12/24 9:52
 **/
public class Writer implements IsCloseable {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(Writer.class);

    /**
     * 数据输出操作
     */
    private Output output;

    /**
     * 线程池实例
     */
    private DataPool dataPool;

    /**
     * 文件读取完毕标志
     */
    private boolean close = false;

    public Writer(DataPool dataPool) {
        this.dataPool = dataPool;
        output = new MergeFileOutput();
    }

    @Sync
    public void executor() {
        while (!dataPool.isClose()) {
            String value = dataPool.poll();
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            output.take(value);
        }
        close = true;
        output.end();
    }

    @Override
    public boolean isClose() {
        return close;
    }
}
