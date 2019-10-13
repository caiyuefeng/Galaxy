package com.galaxy.saturn.store;

import com.galaxy.saturn.SaturnConfiguration;
import com.galaxy.saturn.zookeeper.ZkClient;
import com.galaxy.saturn.zookeeper.ZkNode;
import com.galaxy.saturn.zookeeper.ZkQueue;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 数据缓存池
 * @date : 2018/12/24 9:54
 **/
public class ZKDataPool implements DataPool {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(ZKDataPool.class);

    private static final String MACHINE_PATH = "/galaxy/saturn/machine";
    private static final String COMPLETE_PATH = "/galaxy/saturn/complete";

    /**
     * 分布式数据队列
     */
    private ZkQueue distributeDataQueue;

    /**
     * 本地数据队列
     */
    private LinkedBlockingQueue<String> blockingDeque;

    /**
     * 本地数据集完成节点
     */
    private ZkNode completeNode;

    /***
     * 注册机器IP节点
     */
    private ZkNode registerNode;

    /**
     * 数据池最大缓存量
     */
    private long maxSize = -1L;

    /**
     * 文件处理状态
     * true 表示处理完毕
     * false 表示未处理完毕
     */
    private Map<String, Boolean> status = new HashMap<>();

    /**
     * 输入总条数
     */
    private long inputCnt = 0L;

    /**
     * 输出总条数
     */
    private long outputCnt = 0L;

    /**
     * 该标志用于控制本地数据是否上传分布式数据队列
     * false 只使用本地内存缓存队列
     * true 同时使用本地内存缓存队列和分布式缓存队列
     */
    private boolean upload = false;

    /**
     * 该标志用于控制是否从分布式队列读取数据
     */
    private boolean download = false;

    private boolean isClose = false;

    /**
     * ZK客户端实例
     */
    private ZkClient client;

    private void init(int maxSize) {
        this.maxSize = maxSize;
        this.blockingDeque = new LinkedBlockingQueue<>();
        this.distributeDataQueue = new ZkQueue(client, maxSize);
        client.create(client.prepareNode().addNodePath(MACHINE_PATH).build());
        client.create(client.prepareNode().addNodePath(COMPLETE_PATH).build());
        this.registerNode = client.prepareNode()
                .addNodePath(MACHINE_PATH + "/" + SaturnConfiguration.LOCAL_IP)
                .build();
        this.completeNode = client.prepareNode()
                .addNodePath(COMPLETE_PATH + "/" + SaturnConfiguration.LOCAL_IP)
                .build();
        client.create(registerNode, CreateMode.EPHEMERAL);
        client.create(completeNode);
    }

    private ZKDataPool() {
    }

    public static ZKDataPool getInstance(ZkClient client) {
        ZKDataPool dataPool = new ZKDataPool();
        dataPool.client = client;
        SaturnConfiguration.load();
        dataPool.init(SaturnConfiguration.MAX_SIZE);
        return dataPool;
    }

    /**
     * 插入数据到数据池
     *
     * @param value 待插入值
     */
    @Override
    public void put(String value) {
        try {
            if (upload && blockingDeque.size() >= maxSize) {
                distributeDataQueue.put(value);
                inputCnt++;
                return;
            }
            blockingDeque.put(value);
            inputCnt++;
        } catch (Exception e) {
            LOG.error("数据插入失败");
            e.printStackTrace();
        }
    }

    /**
     * 从数据缓存池中获取值
     *
     * @return 值
     */
    @Override
    public String poll() {
        try {
            if (download && blockingDeque.isEmpty()) {
                String value = distributeDataQueue.poll();
                if (value != null) {
                    outputCnt++;
                }
                return value;
            }
            String value = blockingDeque.poll();
            if (value != null) {
                outputCnt++;
            }
            return value;
        } catch (Exception e) {
            LOG.error("数据获取失败!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmpty() {
        if (!isLocalComplete()) {
            return false;
        }

        try {
            if (!distributeDataQueue.isEmpty()) {
                return false;
            }
            List<ZkNode> registerNodeList = client.list("/galaxy/saturn/machine");
            if (registerNodeList.isEmpty()) {
                return true;
            }
            List<ZkNode> completeNodeList = client.list("/galaxy/saturn/complete");
            return getNodeName(completeNodeList).containsAll(getNodeName(completeNodeList));
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private List<String> getNodeName(List<ZkNode> zkNodes) {
        List<String> nodeList = new ArrayList<>();
        for (ZkNode zkNode : zkNodes) {
            nodeList.add(StringUtils.substringBeforeLast(zkNode.getNodePath(), "/"));
        }
        return nodeList;
    }

    /**
     * 检查本地数据集是否处理完毕
     *
     * @return true 处理完毕 false未处理完毕
     */
    public boolean isLocalComplete() {
        // 检查内存是否为空
        if (!blockingDeque.isEmpty()) {
            return false;
        }
        // 检查文件是否读取完毕
        for (boolean stat : status.values()) {
            if (!stat) {
                return false;
            }
        }
        return true;
    }

    public boolean isDistributeComplete() {
        try {
            return client.list("/galaxy/saturn/complete").size() > 0;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void submitDistribute() {
        this.upload = true;
    }

    public void requestDistribute() {
        client.create(completeNode);
        this.download = true;
    }

    public long getInputCnt() {
        return inputCnt;
    }

    public long getOutputCnt() {
        return outputCnt;
    }

    public long[] getAllSize() {
        long[] buffer = new long[2];
        buffer[0] = blockingDeque.size();
        buffer[1] = distributeDataQueue.size();
        return buffer;
    }

    @Override
    public void close() {
        blockingDeque.clear();
        distributeDataQueue.close();
        client.delete(client.prepareNode().addNodePath("/galaxy/saturn/complete").build());
        client.delete(client.prepareNode().addNodePath("/galaxy/saturn/machine").build());
        completeNode = null;
        status.clear();
        maxSize = -1L;
        inputCnt = 0L;
        outputCnt = 0L;
        upload = false;
        download = false;
        isClose = true;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    @Override
    public boolean isClose() {
        return isClose;
    }
}
