package com.galaxy.sun.demo;

import com.galaxy.sun.base.DataType;
import com.galaxy.sun.base.FileNameType;
import com.galaxy.sun.hadoop.context.WrappedContext;
import com.galaxy.sun.hadoop.mr.BasePartitionReduce;
import org.apache.hadoop.io.Text;

import java.io.IOException;

import static com.galaxy.sun.base.ConstantChar.UNDERLINE;
import static com.galaxy.sun.base.ConstantCounter.CODE_102;
import static com.galaxy.sun.base.ConstantCounter.GROUP_100;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2018/12/11 15:46
 **/
public class DemoReduceThree extends BasePartitionReduce<Text, Text> {
    @Override
    public void setup(WrappedContext wrappedContext) {

    }

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
            context.setDefaultPart(context.getDefaultPart() + UNDERLINE + FileNameType.TOTAL.getValue());
        } else {
            context.setDefaultPart(context.getDefaultPart() + UNDERLINE + FileNameType.IMPORT.getValue());
        }
        context.getCounter(GROUP_100, CODE_102).increment(1);
        context.write(key + "\t" + cnt);
    }
}
