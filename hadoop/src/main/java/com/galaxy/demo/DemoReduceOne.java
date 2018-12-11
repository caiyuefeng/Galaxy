package com.galaxy.demo;

import com.galaxy.hadoop.context.WrappedContext;
import com.galaxy.hadoop.mr.BasePartitionReduce;

import javax.xml.soap.Text;
import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/11 15:46
 **/
public class DemoReduceOne extends BasePartitionReduce<Text, Text> {
    @Override
    public void reduce(String key, Iterable<Text> values, WrappedContext context) throws IOException, InterruptedException {
        context.write(key);
    }
}
