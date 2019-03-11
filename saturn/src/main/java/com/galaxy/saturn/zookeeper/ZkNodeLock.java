package com.galaxy.saturn.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 分布式文件锁
 * @date : 2018/12/24 9:52
 **/
public class ZkNodeLock {

    private final static Logger LOG = LoggerFactory.getLogger(ZkNodeLock.class);

    private String lockPath;

    private ZkClient client;

    private String lockName = "";

    /**
     * 锁询问计算器
     */
    private long inquiry = 1L;

    private ZkNode lockNode;

    private LockWatcher watcher = new LockWatcher();

    /**
     * 锁最大询问次数
     * 该次数用于限制下游锁询问上游锁的次数，当查过改次数限制时，则抛出异常
     */
    private static final long MAX_COUNT = 100000L;

    public ZkNodeLock(String lockPath, ZkClient client) {
        this(lockPath, client, "lock_");
    }

    public ZkNodeLock(String lockPath, ZkClient client, String lockName) {
        this.lockPath = lockPath;
        this.client = client;
        this.lockName = lockName;
        this.lockNode = new ZkNode();
        this.lockNode.setNodePath(this.lockPath);
        client.create(this.lockNode);
        this.lockNode = null;
    }

    /**
     * 加锁
     *
     * @throws InterruptedException 1
     * @throws KeeperException      2
     */
    public void lock() throws InterruptedException, KeeperException {
        while (true) {
            if (MAX_COUNT < inquiry) {
                throw new IllegalStateException("");
            }
            if (lockNode == null) {
                lockNode = new ZkNode();
                lockNode.setNodePath(lockPath + "/" + lockName);
                if (!client.create(lockNode, CreateMode.EPHEMERAL_SEQUENTIAL)) {
                    lockNode = null;
                    Thread.sleep(inquiry);
                    inquiry++;
                    continue;
                }
                lockNode.setNodeSerialNo(Long.parseLong(lockNode.getNodePath()));
            }

            ZkNode destNode = getDestNode();

            // 获取到锁权限则停止自旋
            if (destNode == null || !client.exist(destNode, watcher)) {
                LOG.debug(lockNode.getNodePath() + "获取到分布式锁权限");
                break;
            }
            LOG.debug(lockNode.getNodePath() + "正在监听:" + destNode.getNodePath());
            Thread.sleep(inquiry);
            inquiry++;
        }
    }

    /**
     * 解锁
     */
    public void unlock() {
        if (client.delete(lockNode)) {
            LOG.debug("分布式锁节点:[" + lockNode.getNodePath() + "] 已经释放");
            lockNode = null;
            inquiry = 1L;
            return;
        }
        LOG.error("分布式锁节点:[" + lockNode.getNodePath() + "] 删除失败");
    }

    /**
     * 获取当前锁监听的上游锁
     * 上游锁为序列号小于且最接近于当前锁序列号的锁实例
     *
     * @return 监听的目标所实例
     * @throws KeeperException      1
     * @throws InterruptedException 2
     */
    private ZkNode getDestNode() throws KeeperException, InterruptedException {
        // 获取当前所有节点
        SortedSet<ZkNode> allNodes = new TreeSet<>(client.list(lockPath));
        if (allNodes.isEmpty()) {
            return null;
        }
        // 获取当前所有锁节点
        SortedSet<ZkNode> lockNodes = new TreeSet<>();
        for (ZkNode zkNode : allNodes) {
            if (zkNode.getNodePath().contains(lockName)) {
                lockNodes.add(zkNode);
            }
        }

        if (lockNodes.isEmpty()) {
            return null;
        }
        // 获取优先级高于当前锁的全部上游锁
        SortedSet<ZkNode> headNodes = lockNodes.headSet(lockNode);
        if (headNodes.isEmpty()) {
            return null;
        }
        return headNodes.last();
    }

    /**
     * 监听器
     */
    private class LockWatcher implements Watcher {
        @Override
        public void process(WatchedEvent watchedEvent) {
            try {
                lock();
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }
    }

}
