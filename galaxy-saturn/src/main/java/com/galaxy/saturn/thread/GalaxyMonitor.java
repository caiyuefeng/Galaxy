package com.galaxy.saturn.thread;

import com.galaxy.saturn.core.Writer;
import com.galaxy.saturn.store.DataPool;
import com.galaxy.sirius.annotation.Sync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 监控线程
 * 该线程固定时间间隔轮询访问打印当前处理的输入量、输出量和数据池缓存量
 * @date : 2018/12/24 9:54
 **/
public class GalaxyMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(GalaxyMonitor.class);

    /**
     * 上次打印时间
     */
    private long lastPrintTime = 0L;

    /**
     * 数据缓存池
     */
    private DataPool dataPool;

    /**
     * 消费者缓存队列
     */
    private List<Writer> writers;

    public GalaxyMonitor() {
        dataPool = DataPool.getInstance();
        writers = new ArrayList<>();
    }

    @Sync
    public void monitor() {
        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPrintTime >= 100) {
                // 打印当前数据量统计
                long[] buffer = dataPool.getAllSize();
                LOG.info("数据池统计\t输入数据量:{}\t输出数据量:{}\t当前数据池数据量:{}\t内存数据量:{}\tZookeeper数据量:{}",
                        dataPool.getInputCnt(), dataPool.getOutputCnt(), buffer[0] + buffer[1], buffer[0], buffer[1]);
                lastPrintTime = currentTime;
                // 若本地数据集完成则Zookeeper上注册表示该机器IP
                // 开启分布式缓存队列等待其他机器上传数据
                if (dataPool.isLocalComplete()) {
                    dataPool.requestDistribute();
                }
                if (dataPool.isDistributeComplete()) {
                    dataPool.submitDistribute();
                }

                try {
                    if (dataPool.isEmpty()) {
                        for (Writer writer : writers) {
                            writer.setStop(true);
                        }
                        // 自旋等待所有线程关闭
                        while (true) {
                            Thread.sleep(writers.size());
                            boolean allStop = true;
                            for (Writer writer : writers) {
                                if (!writer.isAlreadyDie()) {
                                    allStop = false;
                                    break;
                                }
                            }
                            if (allStop) {
                                break;
                            }
                        }
                        LOG.info("监控线程停止运行!");
//                        dataPool.close();
//                        break;
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Writer> getWriters() {
        return writers;
    }
}
