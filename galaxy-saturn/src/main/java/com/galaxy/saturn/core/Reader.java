package com.galaxy.saturn.core;

import com.galaxy.saturn.store.DataPool;
import com.galaxy.saturn.utils.FileUtils;
import com.galaxy.sirius.annotation.Sync;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 读取线程
 * @date : 2018/12/24 9:52
 **/
public class Reader {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(Reader.class);

    /**
     * 线程名称
     */
    private String threadName;

    /**
     * 数据池
     */
    private DataPool dataPool = DataPool.getInstance();

    /**
     * 输入操作
     */
    private Input input;

    /**
     * 待处理文件集
     */
    private List<String> files;

    public Reader(List<String> files, String threadName) {
        this.threadName = threadName;
        this.files = files;
        this.input = DefaultInput.getInstance();
        LOG.info("线程:" + threadName + "正在处理文件集:" + new Gson().toJson(files));
    }

    @Sync
    public void run() {
        // 设置当前线程名
        Thread.currentThread().setName(StringUtils.substringBeforeLast(Thread.currentThread().getName(), "_")
                + "_" + threadName);
        try {
            for (String file : files) {
                long sTime = System.currentTimeMillis();
                File temp = new File(file);
                String name = temp.getName();
                this.dataPool.setStatus(name, false);
                LOG.info("文件:" + name + "开始读取...");
                List<String> lines = FileUtils.listLines(temp);
                long cnt = 0L;
                for (String line : lines) {
                    dataPool.put(input.take(line));
                    cnt++;
                }
                LOG.info("文件:[{}] 已经处理完毕!文件条数:{},耗时:{} ms", name, cnt, System.currentTimeMillis() - sTime);
                dataPool.setStatus(name, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
