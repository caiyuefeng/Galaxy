package com.galaxy.saturn.zookeeper;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: ZK操作节点
 * @date : 2018/12/24 9:51
 **/
public class ZkNode implements Comparable<ZkNode> {

    /**
     * 节点路径
     */
    private String nodePath;

    /**
     * 节点序列号
     */
    private long nodeSerialNo = 0L;

    /**
     * 节点存储内容
     */
    private byte[] content = null;


    @Override
    public int hashCode() {
        int result = Objects.hash(nodePath);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZkNode zkNode = (ZkNode) o;
        return nodeSerialNo == zkNode.nodeSerialNo &&
                Objects.equals(getNodePath(), zkNode.getNodePath()) &&
                Arrays.equals(getContent(), zkNode.getContent());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        if (content == null) {
            return "";
        }
        return new String(content);
    }

    @Override
    public int compareTo(ZkNode that) {
        if (this.nodeSerialNo == 0L) {
            return -1;
        }
        if (that.nodeSerialNo == 0L) {
            return 1;
        }
        return (int) (this.nodeSerialNo - that.nodeSerialNo);
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public long getNodeSerialNo() {
        return nodeSerialNo;
    }

    public void setNodeSerialNo(long nodeSerialNo) {
        this.nodeSerialNo = nodeSerialNo;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
