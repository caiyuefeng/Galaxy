package com.galaxy.sun.demo;

import com.galaxy.sun.hadoop.context.WrappedContext;
import com.galaxy.sun.hadoop.mr.BasePartitionReduce;

import javax.xml.soap.Text;
import java.io.IOException;

import static com.galaxy.sun.base.ConstantCounter.CODE_102;
import static com.galaxy.sun.base.ConstantCounter.GROUP_100;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/11 15:46
 **/
public class DemoReduceOne extends BasePartitionReduce<Text, Text> {
    @Override
    public void reduce(String key, Iterable<Text> values, WrappedContext context) throws IOException, InterruptedException {
        context.getCounter(GROUP_100,CODE_102).increment(1);
        context.write(key);
    }
}
