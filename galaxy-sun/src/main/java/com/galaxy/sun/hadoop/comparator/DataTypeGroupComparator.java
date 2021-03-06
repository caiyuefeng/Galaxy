package com.galaxy.sun.hadoop.comparator;

import com.galaxy.sun.hadoop.writable.DataTypeKey;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  新旧数据分组器
 * @date 2018/12/11 14:35
 **/
public class DataTypeGroupComparator extends WritableComparator {

    protected DataTypeGroupComparator() {
        super(DataTypeKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        DataTypeKey first = (DataTypeKey) a;
        DataTypeKey second = (DataTypeKey) b;
        return first.getValue().compareTo(second.getValue());
    }
}
