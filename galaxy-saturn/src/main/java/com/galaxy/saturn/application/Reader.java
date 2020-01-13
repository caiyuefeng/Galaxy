package com.galaxy.saturn.application;

import com.galaxy.earth.FileUtils;
import com.galaxy.saturn.IsCloseable;
import com.galaxy.saturn.store.DataPool;
import com.galaxy.sirius.annotation.Sync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  文件读取器
 * @date 2018/12/24 9:52
 **/
public class Reader implements IsCloseable {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(Reader.class);

    /**
     * 数据池
     */
    private DataPool dataPool;

    /**
     * 输入操作
     */
    private Input input;

    private boolean close = false;

    /**
     * 待处理文件集
     */
    private List<String> localFiles;

    public Reader(DataPool dataPool, List<String> files) {
        this.dataPool = dataPool;
        this.localFiles = files;
        this.input = DefaultInput.getInstance();
    }

    @Sync
    public void executor() {
        try {
            for (String file : localFiles) {
                LOG.info("文件:[{}]开始读取...", file);
                long sTime = System.currentTimeMillis();
                List<String> lines = FileUtils.listLines(new File(file));
                long cnt = 0L;
                for (String line : lines) {
                    dataPool.put(input.take(line));
                    cnt++;
                }
                LOG.info("文件:[{}] 已经处理完毕!文件条数:{},耗时:{} ms", file, cnt, System.currentTimeMillis() - sTime);
            }
        } catch (IOException e) {
            LOG.error("读取文件发生异常!", e);
        }
        close = true;
    }

    @Override
    public boolean isClose() {
        return this.close;
    }
}
