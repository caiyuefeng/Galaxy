package com.galaxy.sun.hadoop.context;

import com.galaxy.sun.compress.DataCompress;
import com.galaxy.sun.partitioner.DataPartitioner;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 分区任务Reduce上下文包装器
 * @date : 2018/12/11 9:59
 **/
public class WrappedReducePartitionContext<K, V> implements WrappedContext {

    /**
     * Reduce默认上下文
     */
    private Reducer<K, V, Text, Text>.Context context;

    /**
     * 分区信息
     */
    private String defaultPart;

    /**
     * 分区器
     */
    private DataPartitioner<String> partitioner;

    /**
     * 压缩器
     */
    private DataCompress compress;

    private Text nodeKey = new Text();

    private Text nodeValue = new Text();

    /**
     * Map阶段分区标志
     */
    private boolean mapAlreadyPartition = false;

    public WrappedReducePartitionContext(Reducer<K, V, Text, Text>.Context context) {
        this.context = context;
    }

    public void init(DataPartitioner<String> func1, DataCompress func2) {
        partitioner = func1;
        compress = func2;
    }

    @Override
    @Deprecated
    public void write(String key, String value, String sortSeed) throws IOException, InterruptedException {
        write(key, value);
    }

    /**
     * 输出分区信息 和 真实值
     * 如果传入的分区信息为空则使用默认的分区信息
     *
     * @param key   分区信息
     * @param value 真实值
     * @throws IOException          1
     * @throws InterruptedException 2
     */
    @Override
    public void write(String key, String value) throws IOException, InterruptedException {
        nodeKey.set(StringUtils.isEmpty(key) ? defaultPart : key);
        nodeValue.set(compress.compress(value));
        context.write(nodeKey, nodeValue);
    }

    /**
     * 直接输出真实值
     * 如果Map阶段未进行分区则使用当前的分区器对数据进行分区
     * 如果Map阶段已经进行分区则使用Map阶段的分区信息
     *
     * @param key 真实值
     * @throws IOException          1
     * @throws InterruptedException 2
     */
    @Override
    public void write(String key) throws IOException, InterruptedException {
        write(mapAlreadyPartition ? defaultPart : partitioner.encode(key), key);
    }

    @Override
    public Counter getCounter(String group, String item) {
        return context.getCounter(group, item);
    }

    @Override
    public void setDefaultPart(String part) {
        defaultPart = part;
    }

    @Override
    public String getDefaultPart() {
        return defaultPart;
    }

    @Override
    public Configuration getConfiguration() {
        return context.getConfiguration();
    }

    public void setMapAlreadyPartition(boolean mapAlreadyPartition) {
        this.mapAlreadyPartition = mapAlreadyPartition;
    }
}
