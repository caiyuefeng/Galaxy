package com.galaxy.saturn.zookeeper;

import com.galaxy.saturn.SaturnConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 14:35 2019/10/13
 *
 */
public class ZkClientTest {

    private ZkClient client;

    @Before
    public void init() {
        client = ZkClient.getInstance(SaturnConfiguration.ZK_MACHINE_IP);
    }

    @Test
    public void existNode() {
        Assert.assertFalse(client.exist(client.prepareNode().addNodePath("/galaxy").build()));
        Assert.assertFalse(client.exist(client.prepareNode().addNodePath("/galaxy/saturn").build()));
        Assert.assertFalse(client.exist(client.prepareNode().addNodePath("/galaxy/saturn/test").build()));
    }

    @Test
    public void createNode() {
        Assert.assertTrue(client.create(client.prepareNode().addNodePath("/galaxy/saturn/test/node1").build()));
        Assert.assertTrue(client.create(client.prepareNode().addNodePath("/galaxy/saturn/test1/node2").build()));
        Assert.assertTrue(client.create(client.prepareNode().addNodePath("/galaxy/saturn/machine/127.0.0.1").build()));
    }
}