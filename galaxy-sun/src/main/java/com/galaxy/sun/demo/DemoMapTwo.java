package com.galaxy.sun.demo;

import com.galaxy.sun.hadoop.context.WrappedContext;
import com.galaxy.sun.hadoop.mr.BaseSecondarySortMap;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

import static com.galaxy.sun.base.ConstantCounter.CODE_201;
import static com.galaxy.sun.base.ConstantCounter.GROUP_200;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2018/12/11 15:46
 **/
public class DemoMapTwo extends BaseSecondarySortMap<LongWritable, Text> {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void setup(WrappedContext context) {

    }

    @Override
    public void map(String value, WrappedContext context) throws IOException, InterruptedException {
        if (StringUtils.isEmpty(value)) {
            context.getCounter(GROUP_200, CODE_201).increment(1);
            return;
        }
        String[] values = value.split("\t", -1);
        builder.setLength(0);
        builder.append(values[2]).append("\t")
                .append(values[3]).append("\t")
                .append(values[4]).append("\t")
                .append(values[5]);
        String key = builder.toString();
        builder.setLength(0);
        builder.append(values[0]).append("\t")
                .append(values[1]);
        context.write(key, builder.toString());
    }
}
