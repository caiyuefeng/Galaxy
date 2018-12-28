package com.galaxy.sun.partitioner;

import com.google.gson.Gson;

import java.util.Map;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 时间分区器
 * @date : 2018/12/11 15:41
 **/
public class TimePartitioner<Type> implements DataPartitioner<Type> {

    private Gson gson = new Gson();

    private String[] values = {"", ""};

    @Override
    public String encode(Type o) {
        Map<String, String> valueMap = gson.fromJson(o.toString(), Map.class);
        return valueMap.get("deptime").substring(0, 8) + "_IMPORT";
    }

    @Override
    public String[] decode(Type o) {
        String temp = o.toString();
        int index = temp.lastIndexOf("\t");
        // 去除制表符
        values[0] = temp.substring(index+1, temp.length());
        values[1] = temp.substring(0, index);
        return values;
    }
}
