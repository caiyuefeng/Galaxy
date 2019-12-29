package com.galaxy.saturn.zookeeper;

import com.galaxy.stone.Symbol;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/24 9:51
 **/
public class ZkClient implements Watcher {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(ZkClient.class);

    /**
     * 权限列表
     */
    private List<ACL> defaultAcl = ZooDefs.Ids.OPEN_ACL_UNSAFE;

    /**
     * 客户端实例
     */
    private volatile static ZkClient CLIENT = null;

    /**
     * 客户端实例
     */
    private ZooKeeper zk;

    private ZkClient() {
    }

    private void init(String connectInfo) throws IOException {
        zk = new ZooKeeper(connectInfo, 2000, CLIENT);
    }

    public static ZkClient getInstance(String connectionInfo) {
        if (CLIENT == null) {
            synchronized (ZkClient.class) {
                if (CLIENT == null) {
                    CLIENT = new ZkClient();
                    try {
                        CLIENT.init(connectionInfo);
                    } catch (IOException e) {
                        LOG.error("Zookeeper客户端初始化失败!", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return CLIENT;
    }

    public ZkNodeBuilder prepareNode() {
        return new ZkNodeBuilder();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
    }

    public void delete(ZkNode zkNode) throws KeeperException, InterruptedException {
        String path = zkNode.getNodePath();
        path = path.endsWith(Symbol.SLASH.getValue()) ? path.substring(0, path.length() - 1) : path;
        if (zk.exists(path, false) == null) {
            return;
        }
        List<String> children = zk.getChildren(path, false);
        for (String child : children) {
            delete(prepareNode().addNodePath(path + "/" + child).build());
        }
        zk.delete(path, -1);
    }

    public boolean create(ZkNode zkNode) {
        return create(zkNode, null);
    }

    public boolean create(ZkNode zkNode, Watcher watcher) {
        try {
            System.out.println(zkNode.toString());
            System.out.println(zkNode.getNodePath() + "创建时间:" + System.currentTimeMillis());
            ZkNode parent = zkNode.getParent();
            if (parent != null && !exist(parent, watcher)) {
                create(parent);
            }
            if (!exist(zkNode, watcher)) {
                zk.create(zkNode.getNodePath(), zkNode.getContent(), defaultAcl, zkNode.getCreateMode());
            }
        } catch (KeeperException | InterruptedException e) {
            LOG.error(String.format("节点[%s]创建失败!", zkNode.getNodePath()), e);
            return false;
        }
        return true;
    }

    boolean exist(ZkNode zkNode) {
        return exist(zkNode, null);
    }

    boolean exist(ZkNode zkNode, Watcher watcher) {
        try {
            if (zk.exists(zkNode.getNodePath(), watcher) == null) {
                return false;
            }
        } catch (KeeperException | InterruptedException e) {
            LOG.error("连接失败!", e);
            e.fillInStackTrace();
        }
        return true;
    }

    public List<ZkNode> list(String path) throws KeeperException, InterruptedException {
        List<String> nodeNames = zk.getChildren(path, null);
        List<ZkNode> zkNodes = new ArrayList<>();
        for (String nodeName : nodeNames) {
            ZkNode node = prepareNode().addNodePath(path + "/" + nodeName).build();
            if (nodeName.contains("_")) {
                node.setNodeSerialNo(Long.parseLong(nodeName));
            }
            zkNodes.add(node);
        }
        return zkNodes;
    }

    int getSize(String path) {
        try {
            if (zk.exists(path, false) != null) {
                List<String> list = zk.getChildren(path, false);
                return list == null ? 0 : list.size();
            }
        } catch (KeeperException | InterruptedException e) {
            LOG.error(String.format("路径:[%s]获取异常!", path), e);
        }
        return 0;
    }

    String getContent(ZkNode node) {
        if (!exist(node)) {
            return null;
        }
        try {
            return new String(zk.getData(node.getNodePath(), false, null), StandardCharsets.UTF_8);
        } catch (KeeperException | InterruptedException e) {
            LOG.error(String.format("获取[%s]节点内容时发生异常!\n", node.getNodePath()), e);
        }
        return null;
    }

    public static class ZkNodeBuilder {

        private String nodePath;

        private String nodeSerialNo = "0";

        private byte[] content;

        private CreateMode createMode = CreateMode.PERSISTENT;

        public ZkNodeBuilder addNodePath(String nodePath) {
            this.nodePath = nodePath;
            return this;
        }

        public ZkNodeBuilder addSerialNo(String nodeSerialNo) {
            this.nodeSerialNo = nodeSerialNo;
            return this;
        }

        public ZkNodeBuilder addContent(String content) {
            this.content = content.getBytes();
            return this;
        }

        public ZkNodeBuilder addCreateMode(CreateMode mode) {
            this.createMode = mode;
            return this;
        }

        public ZkNode build() {
            return new ZkNode(this.nodePath, Long.parseLong(this.nodeSerialNo), this.content, this.createMode);
        }
    }
}
