package com.galaxy.sun.partitioner;

import com.galaxy.sun.base.FileNameType;

import static com.galaxy.sun.base.ConstantChar.UNDERLINE;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  身份证关联数据分区器
 * @date 2018/12/11 15:35
 **/
public class DoubleRelationPartitioner implements DataPartitioner {

    private final String[] defaultBuffer = {"", ""};

    @Override
    public String encode(Object o) {
        String[] values = o.toString().split("\t", -1);
        return values[1].substring(0, 6) + UNDERLINE + values[1].substring(6, 10) + UNDERLINE + FileNameType.IMPORT.getValue();
    }

    @Override
    public String[] decode(Object o) {
        return defaultBuffer;
    }
}
