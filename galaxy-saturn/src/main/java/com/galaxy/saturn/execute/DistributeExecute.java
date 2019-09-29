package com.galaxy.saturn.execute;

import com.galaxy.saturn.core.DistributeFile;
import com.galaxy.saturn.core.Reader;
import com.galaxy.saturn.core.Writer;
import com.galaxy.saturn.store.DataPool;
import com.galaxy.saturn.store.SaturnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 执行器
 * @date : 2018/12/24 9:54
 **/
public class DistributeExecute {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(DistributeExecute.class);

    public void execute() {
        Thread.currentThread().setName("Saturn_主线程");
        LOG.info("开始执行...");

        DataPool dataPool = DataPool.getInstance();
        List<String> paths = new ArrayList<>();
        for (File file : new File("./input/").listFiles()) {
            paths.add(file.getAbsolutePath());
        }
        LOG.info("文件数量:" + paths.size());
        Map<String, List<String>> map = DistributeFile.take(paths, 0, 1,
                SaturnConfiguration.READER_NUM);
        for (int i = 0; i < SaturnConfiguration.READER_NUM; i++) {
            dataPool.submit(new Reader(map.get(String.valueOf(i)), "生产_" + i));
        }
        for (int i = 0; i < SaturnConfiguration.WRITER_NUM; i++) {
            dataPool.submit(new Writer("消费_" + i));
        }
        dataPool.getThreadPool().shutdown();
        while (true) {
            if (dataPool.getThreadPool().isTerminated()) {
                LOG.info("执行完毕!");
                break;
            }
        }
    }


}
