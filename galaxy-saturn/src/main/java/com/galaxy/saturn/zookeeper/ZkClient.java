package com.galaxy.saturn.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    private List<ACL> defaultAcl = ZooDefs.Ids.OPEN_ACL_UNSAFE;

    /**
     * 客户端实例
     */
    private static ZkClient client = null;

    /**
     * 客户端实例
     */
    private ZooKeeper zk;

    private ZkClient() {
    }

    public static void init(String connectInfo) throws IOException {
        client = new ZkClient();
        client.zk = new ZooKeeper(connectInfo, 2000, client);

        ZkNode node = new ZkNode();
        node.setNodePath("/galaxy");
        if (!client.exist(node) && !client.create(node)) {
            client = null;
            return;
        }
        node.setNodePath("/galaxy/saturn");
        if (!client.exist(node) && !client.create(node)) {
            client = null;
        }
    }

    public static ZkClient getInstance() {
        return client;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
    }

    public boolean delete(ZkNode zkNode) {
        return delete(zkNode.getNodePath());
    }

    public boolean delete(String path) {
        path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        try {
            if (zk.exists(path, false) == null) {
                return true;
            }
            List<String> children = zk.getChildren(path, false);
            if (children == null || children.isEmpty()) {
                zk.delete(path, -1);
                return true;
            }
            for (String child : children) {
                delete(path + "/" + child);
            }
            zk.delete(path, -1);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean create(ZkNode zkNode) {
        return create(zkNode, CreateMode.PERSISTENT);
    }

    public boolean create(ZkNode zkNode, CreateMode mode) {
        try {
            zkNode.setNodePath(zk.create(zkNode.getNodePath(),zkNode.getContent(),defaultAcl,mode));
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            if(e.getMessage().contains("NodeExists")){
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean exist(ZkNode zkNode) {
        return exist(zkNode, null);
    }

    public boolean exist(ZkNode zkNode, Watcher watcher) {
        try {
            if (zk.exists(zkNode.getNodePath(), watcher) == null) {
                return false;
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<ZkNode> list(String path) throws KeeperException, InterruptedException {
        List<String> nodeNames = zk.getChildren(path,null);
        List<ZkNode> zkNodes = new ArrayList<>();
        for(String nodeName : nodeNames){
            ZkNode node = new ZkNode();
            node.setNodePath(path+"/"+nodeName);
            if(nodeName.contains("_")){
                node.setNodeSerialNo(Long.parseLong(nodeName));
            }
            zkNodes.add(node);
        }
        return zkNodes;
    }

    public int getSize(String path){
        try {
            return zk.getChildren(path,false).size();
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getContent(ZkNode node){
        if(!exist(node)){
            return null;
        }
        try {
            return new String(zk.getData(node.getNodePath(),false,null),"UTF-8");
        } catch (UnsupportedEncodingException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
