package com.galaxy.sun.partitioner;

import org.apache.commons.lang.StringUtils;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/11 15:38
 **/
public class GetCurrentPartitioner implements DataPartitioner {
    @Override
    public String encode(Object o) {
        String name = StringUtils.substringAfterLast(o.toString(), "/");
        String part = name.substring(name.indexOf("PS") + 2, name.indexOf("PE"));
        return StringUtils.substringBeforeLast(part, "_");
    }

    private final String[] values = new String[2];

    @Override
    public String[] decode(Object o) {
        values[0] = StringUtils.substringAfterLast(o.toString(), "\t");
        values[1] = StringUtils.substringBeforeLast(o.toString(), "\t");
        return values;
    }
}
