package com.galaxy.hadoop.context;

import com.galaxy.hadoop.writable.DataTypeKey;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 二次排序 Map上下文包装器
 * @date : 2018/12/11 13:59
 **/
public class WrappedMapSecondarySortContext<K, V> extends WrappedMapPartitionContext<K, V> {

    private Mapper<K, V, DataTypeKey, Text>.Context context;

    private DataTypeKey nodeKey = new DataTypeKey();

    private Text nodeValue = new Text();

    private Text defaultSortSeed;

    public WrappedMapSecondarySortContext(Mapper<K, V, DataTypeKey, Text>.Context context) {
        super(null);
        this.context = context;
    }

    @Override
    public void write(String key, String value, String sortSeed) throws IOException, InterruptedException {
        defaultSortSeed = new Text(sortSeed);
        write(key, value);
    }

    @Override
    public void write(String key, String value) throws IOException, InterruptedException {
        nodeKey.setSort(defaultSortSeed);
        nodeValue.set(value + "\t" + defaultSortSeed);
        nodeKey.setValue(new Text(isPartMark() ? key + "\t" + getDefaultPart() : key));
        context.write(nodeKey, nodeValue);
    }

    @Override
    public void write(String key) throws IOException, InterruptedException {
        nodeKey.setSort(defaultSortSeed);
        nodeKey.setValue(new Text(isPartMark() ? key + "\t" + getDefaultPart() : key));
        nodeValue.set(isPartMark() ? key + "\t" + defaultSortSeed : "\t" + defaultSortSeed);
        context.write(nodeKey, nodeValue);
    }

    public void setDefaultSortSeed(Text defaultSortSeed) {
        this.defaultSortSeed = defaultSortSeed;
    }
}
