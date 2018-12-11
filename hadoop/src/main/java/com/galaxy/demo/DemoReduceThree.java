package com.galaxy.demo;

import com.galaxy.base.DataType;
import com.galaxy.hadoop.context.WrappedContext;
import com.galaxy.hadoop.mr.BasePartitionReduce;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/11 15:46
 **/
public class DemoReduceThree extends BasePartitionReduce<Text, Text> {
    @Override
    public void reduce(String key, Iterable<Text> values, WrappedContext context) throws IOException, InterruptedException {
        long cnt = 0L;
        String dataType = DataType.OLD.getValue();
        for (Text value : values) {
            String[] buffer = value.toString().split("\t", -1);
            cnt += Long.parseLong(buffer[0]);
            if (DataType.NEW.getValue().equals(buffer[1])) {
                dataType = DataType.NEW.getValue();
            }
        }
        if (DataType.OLD.getValue().equals(dataType)) {
            context.setDefaultPart(context.getDefaultPart() + "_TOTAL");
        } else {
            context.setDefaultPart(context.getDefaultPart() + "_IMPORT");
        }
        context.write(key + "\t" + cnt);
    }
}
