package com.galaxy.sun.hadoop.context;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  二次排序 Reduce上下文包装器
 * @date 2018/12/11 14:08
 **/
public class WrappedReduceSecondarySortContext<K, V> extends WrappedReducePartitionContext<K, V> {

    private Reducer<K, V, Text, Text>.Context context;

    public WrappedReduceSecondarySortContext(Reducer<K, V, Text, Text>.Context context) {
        super(context);
        this.context = context;
    }
}
