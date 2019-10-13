package com.galaxy.saturn;

import com.galaxy.earth.FileUtils;
import com.galaxy.saturn.core.Reader;
import com.galaxy.saturn.core.Writer;
import com.galaxy.saturn.store.ZKDataPool;
import com.galaxy.saturn.zookeeper.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: Saturn 客户端
 * @Date : Create in 22:59 2019/10/6
 * @Modified By:
 */
public class SaturnClient implements IsCloseable {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(SaturnClient.class);

    /**
     * 生产者缓存
     */
    private List<Reader> readers = new ArrayList<>();

    /**
     * 消费者缓存
     */
    private List<Writer> writers = new ArrayList<>();

    private ZKDataPool dataPool;

    @SuppressWarnings("WeakerAccess")
    public SaturnClient() {
        dataPool = ZKDataPool.getInstance(ZkClient.getInstance(SaturnConfiguration.ZK_MACHINE_IP));
    }

    private void submit(Reader reader) {
        readers.add(reader);
        reader.executor();
    }

    private void submit(Writer writer) {
        writers.add(writer);
        writer.executor();
    }

    void run() {
        execute();
        try {
            int retry = 10;
            while (!isClose() && retry < 86400) {
                LOG.info("当前线程还在执行当中!");
                Thread.sleep(retry);
                retry += retry;
            }
        } catch (InterruptedException e) {
            LOG.info("线程中断异常!", e);
        }
    }

    private void execute() {
        LOG.info("开始执行...");
        List<String> paths = new ArrayList<>();
        for (File file : FileUtils.getAllFile("./input")) {
            paths.add(file.getAbsolutePath());
        }
        LOG.info("文件数量:" + paths.size());
        Map<String, List<String>> map = Dispatcher.distributeFile(paths, 0, 1, SaturnConfiguration.READER_NUM);

        for (int i = 0; i < SaturnConfiguration.READER_NUM; i++) {
            submit(new Reader(dataPool, map.get(String.valueOf(i))));
        }

        for (int i = 0; i < SaturnConfiguration.WRITER_NUM; i++) {
            submit(new Writer(dataPool));
        }
    }

    @Override
    public boolean isClose() {
        Optional<Boolean> optional1 = readers.stream().map(Reader::isClose).reduce((a, b) -> a && b);
        Optional<Boolean> optional2 = writers.stream().map(Writer::isClose).reduce((a, b) -> a && b);
        return optional1.orElse(false) && optional2.orElse(false);
    }
}
