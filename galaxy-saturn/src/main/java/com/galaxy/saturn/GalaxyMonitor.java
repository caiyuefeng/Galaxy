package com.galaxy.saturn;

import com.galaxy.saturn.store.ZKDataPool;
import com.galaxy.saturn.zookeeper.ZkClient;
import com.galaxy.saturn.zookeeper.ZkNode;
import com.galaxy.sirius.annotation.Sync;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 监控线程
 * 1、心跳功能：定时检测
 * 2、数据池监控功能
 * 3、机器维护
 * 4、输入器输出器维护
 * @date : 2018/12/24 9:54
 **/
public class GalaxyMonitor {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(GalaxyMonitor.class);

    /**
     * 上次打印时间
     */
    private long lastPrintTime = 0L;

    /**
     * 数据缓存池
     */
    private ZKDataPool dataPool;

    private SaturnClient client;

    private ZkClient zkClient;

    public GalaxyMonitor(SaturnClient client, ZkClient zKclient) {
        this.dataPool = ZKDataPool.getInstance(zKclient);
        this.client = client;
        this.zkClient = zKclient;
    }

    private boolean heartBeatDetection() {
        ZkNode node = zkClient.prepareNode()
                .addNodePath("/galaxy/saturn/machine/192.168.1.129").addContent("").build();
        zkClient.create(node, CreateMode.PERSISTENT);
        return true;
    }

    private boolean dataPoolDetection() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPrintTime >= 100) {
            // 打印当前数据量统计
            long[] buffer = dataPool.getAllSize();
            LOG.info("数据池统计\t输入数据量:[{}]\t输出数据量:[{}]\t当前数据池数据量:[{}]\t内存数据量:[{}]\tZookeeper数据量:[{}]",
                    dataPool.getInputCnt(), dataPool.getOutputCnt(), buffer[0] + buffer[1], buffer[0], buffer[1]);
            lastPrintTime = currentTime;
            try {
                if (client.isClose()) {
                    LOG.info("本地客户端已经关闭,即将关闭数据池!");
                    dataPool.close();
                    return false;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean machineDetection() {
        return true;
    }

    private boolean ioDetection() {
        return true;
    }

    @Sync
    public void monitor() {
        while (true) {
            if (heartBeatDetection()) {
                break;
            }
            if (machineDetection()) {
                break;
            }
            if (dataPoolDetection()) {
                break;
            }
            if (ioDetection()) {
                break;
            }
        }

    }
}
