package com.galaxy.sun.hadoop.comparator;

import com.galaxy.sun.base.DataType;
import com.galaxy.sun.hadoop.writable.DataTypeKey;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import java.util.Objects;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  新旧数据排序器
 * 新数据排列靠前，旧数据排列靠后
 * @date 2018/12/11 14:37
 **/
public class DataTypeSortComparator extends WritableComparator {

    public DataTypeSortComparator() {
        super(DataTypeKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        DataTypeKey first = (DataTypeKey) a;
        DataTypeKey second = (DataTypeKey) b;
        boolean fType = Objects.equals(DataType.NEW.getValue(), first.getSort().toString());
        boolean sType = Objects.equals(DataType.NEW.getValue(), second.getSort().toString());
        if (fType == sType) {
            return first.getValue().toString().compareTo(second.getValue().toString());
        }
        return fType ? -1 : 1;
    }
}
