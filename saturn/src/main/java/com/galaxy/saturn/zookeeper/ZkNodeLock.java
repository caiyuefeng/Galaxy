package com.galaxy.saturn.zookeeper;

import org.apache.zookeeper.CreateMode;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/24 9:52
 **/
public class ZkNodeLock {


    private String lockPath;

    private ZkClient client;

    private String lockName = "";

    private long inquiry = 1L;

    private ZkNode lockNode;

    private static final long MAX_COUNT = 100000L;

    public ZkNodeLock(String lockPath, ZkClient client) {
        this(lockPath, client"lock_");
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

    public void lock() throws InterruptedException {
        while (true){
            if(MAX_COUNT<inquiry){
                throw new IllegalStateException("");
            }
            if(lockNode==null){
                lockNode = new ZkNode();
                lockNode.setNodePath(lockPath+"/"+lockName);
                if(!client.create(lockNode, CreateMode.EPHEMERAL_SEQUENTIAL)){
                    lockNode=null;
                    Thread.sleep(inquiry);
                    inquiry++;
                    continue;
                }
                lockNode.setNodeSerialNo(Long.parseLong(lockNode.getNodePath()));
            }

            ZkNode destNode =
        }




    }

}
