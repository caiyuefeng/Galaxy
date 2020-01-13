package com.galaxy.sun.partitioner;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2018/12/11 10:48
 **/
public class DefaultPartitioner<Type> implements DataPartitioner<Type> {

    private String[] defaultDecode = {"", ""};

    @Override
    public String encode(Type o) {
        return "";
    }

    @Override
    public String[] decode(Type o) {
        defaultDecode[1] = o.toString();
        return defaultDecode;
    }
}
