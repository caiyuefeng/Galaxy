package com.galaxy.saturn.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 分布式队列
 * @date : 2018/12/24 9:52
 **/
public class ZkQueue {

    private static final String ROOT_PATH = "/galaxy/saturn/queue";

    /**
     * ZK客户端实例
     */
    private ZkClient client;

    /**
     * 队列存储根路径
     */
    private String rootPath;

    /**
     * 默认的数据节点名称前缀
     */
    private final String defaultName = "data_";

    /**
     * 队列最大的存储数
     */
    private long maxSize;

    private ZkQueue(String rootPath, ZkClient client, long maxSize) {
        this.rootPath = rootPath;
        this.client = client;
        this.maxSize = maxSize;
        ZkNode rootNode = client.prepareNode().addNodePath(rootPath).build();
        client.delete(rootNode);
        client.create(rootNode);
    }

    public ZkQueue(ZkClient client, long maxSize) {
        this(ROOT_PATH + "/queue_tmp_" + System.currentTimeMillis(), client, maxSize);
    }

    /**
     * 向队列中插入值
     *
     * @param content 待插入内容
     * @throws Exception 1
     */
    public void put(String content) throws Exception {
        ZkNodeLock putLock = new ZkNodeLock("/galaxy/saturn/lock_put", client, "queue_lock_");
        putLock.lock();
        try {
            while (maxSize > 0 && size() >= maxSize) {
                Thread.sleep(1);
            }
            ZkNode node = client.prepareNode().addContent(content)
                    .addNodePath(rootPath + "/" + defaultName)
                    .build();
            if (!client.create(node, CreateMode.PERSISTENT_SEQUENTIAL)) {
                throw new Exception("插入数据失败!插入路径:[" + rootPath + "/" + defaultName + "];插入内容:" + content);
            }
        } finally {
            putLock.unlock();
        }
    }

    /**
     * 从队列中获取值
     *
     * @return 值
     * @throws Exception 1
     */
    public String poll() throws Exception {
        ZkNodeLock pollLock = new ZkNodeLock("/galaxy/saturn/lock_poll", client, "queue_lock_");
        pollLock.lock();
        try {
            SortedSet<ZkNode> zkNodeSortedSet = getAllDataNode();
            if (zkNodeSortedSet.isEmpty()) {
                return null;
            }
            ZkNode zkNode = zkNodeSortedSet.first();
            String content = client.getContent(zkNode);
            if (client.delete(zkNode)) {
                return content;
            }
            throw new Exception("数据节点删除失败!节点路径:[" + zkNode.getNodePath() + "]");
        } finally {
            pollLock.unlock();
        }
    }

    public int size() {
        return client.getSize(rootPath);
    }

    /**
     * 获取队列中所有的数据节点，并且按照节点序列号排列
     *
     * @return 数据节点集合
     * @throws KeeperException      1
     * @throws InterruptedException 2
     */
    private SortedSet<ZkNode> getAllDataNode() throws KeeperException, InterruptedException {
        List<ZkNode> zkNodeList = client.list(rootPath);
        SortedSet<ZkNode> zkNodeSortedSet = new TreeSet<>();
        for (ZkNode zkNode : zkNodeList) {
            if (zkNode.getNodePath().contains(defaultName)) {
                zkNodeSortedSet.add(zkNode);
            }
        }
        return zkNodeSortedSet;
    }

    /**
     * 队列判空
     *
     * @return true 当前队列空 false当前队列不空
     * @throws KeeperException      1
     * @throws InterruptedException 2
     */
    public boolean isEmpty() throws KeeperException, InterruptedException {
        return getAllDataNode().size() == 0;
    }

    /**
     * 关闭队列
     */
    public void close() {
        client.delete(client.prepareNode().addNodePath(rootPath).build());
        client.delete(client.prepareNode().addNodePath("/galaxy/saturn/lock_put").build());
        client.delete(client.prepareNode().addNodePath("/galaxy/saturn/lock_poll").build());
        client = null;
        rootPath = null;
    }
}
