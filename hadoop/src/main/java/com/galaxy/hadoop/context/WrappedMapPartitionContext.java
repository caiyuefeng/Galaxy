package com.galaxy.hadoop.context;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 分区Map任务的上下文包装类型
 * @date : 2018/12/11 9:59
 **/
public class WrappedMapPartitionContext<K, V> implements WrappedContext {

    public Mapper<K, V, Text, Text>.Context context;

    private Text nodeKey = new Text();

    private Text nodeValue = new Text();

    private String defaultPart;

    private boolean partMark = false;

    public WrappedMapPartitionContext(Mapper<K, V, Text, Text>.Context context) {
        this.context = context;
    }

    @Override
    @Deprecated
    public void write(String key, String value, String sortSeed) throws IOException, InterruptedException {
        write(key, value);
    }

    @Override
    public void write(String key, String value) throws IOException, InterruptedException {
        nodeKey.set(partMark ? key + "\t" + defaultPart : key);
        nodeValue.set(value);
        context.write(nodeKey, nodeValue);
    }

    @Override
    public void write(String key) throws IOException, InterruptedException {
        nodeKey.set(partMark ? key + "\t" + defaultPart : key);
        nodeValue.set(partMark ? key : "");
        context.write(nodeKey, nodeValue);
    }

    @Override
    public Counter getCounter(String group, String item) {
        return context.getCounter(group, item);
    }

    @Override
    public void setDefaultPart(String part) {
        this.defaultPart = part;
    }

    @Override
    public String getDefaultPart() {
        return this.defaultPart;
    }

    @Override
    public Configuration getConfiguration() {
        return context.getConfiguration();
    }

    public boolean isPartMark() {
        return partMark;
    }

    public void setPartMark(boolean partMark) {
        this.partMark = partMark;
    }
}
