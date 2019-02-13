package com.galaxy.saturn.core;

import com.galaxy.saturn.store.DataPool;
import com.galaxy.saturn.utils.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/24 9:52
 **/
public class Reader implements Runnable {


    /**
     * 线程名称
     */
    private String threadName;

    /**
     * 数据池
     */
    private DataPool dataPool = DataPool.getInstance();

    private Input input;

    private List<String> lines;

    public Reader(List<String> lines, String threadName) {
        this.threadName = threadName;
        this.lines = lines;
        this.input = DefaultInput.getInstance();
    }

    @Override
    public void run() {
        // 设置当前线程名
        Thread.currentThread().setName(StringUtils.substringBeforeLast(Thread.currentThread().getName(), "_") + "_" + threadName);

        for (String file : lines) {
            long sTime = System.currentTimeMillis();
            File temp = new File(file);
            String name = temp.getName();
            this.dataPool.setStatus(name, false);

        }

    }
}
