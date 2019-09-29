package com.galaxy.sun.hadoop.partition;

import com.galaxy.sun.hadoop.writable.DataTypeKey;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 新旧数据Shuffle分区器
 * @date : 2018/12/11 14:32
 **/
public class DataTypePartition extends Partitioner<DataTypeKey, Text> {
    @Override
    public int getPartition(DataTypeKey dataTypeKey, Text text, int i) {
        return (dataTypeKey.getValue().hashCode() & Integer.MAX_VALUE) % i;
    }
}
