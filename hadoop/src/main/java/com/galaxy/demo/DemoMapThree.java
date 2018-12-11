package com.galaxy.demo;

import com.galaxy.base.DataType;
import com.galaxy.hadoop.context.WrappedContext;
import com.galaxy.hadoop.mr.BasePartitionMap;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.galaxy.base.ConstantCounter.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/11 15:46
 **/
public class DemoMapThree extends BasePartitionMap<LongWritable, Text, Text> {

    private String dataType = DataType.OLD.getValue();

    @Override
    protected void setup(Context context) {
        super.setup(context);
        try {
            String fileName = getCurrentFileName(context);
            if (fileName.contains("IMPORT")) {
                dataType = DataType.NEW.getValue();
            } else if (fileName.contains("TOTAL")) {
                dataType = DataType.OLD.getValue();
            }
            super.context.setDefaultPart(super.partitioner.encode(fileName));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean take(LongWritable key, Text value, WrappedContext context) {
        // 统计总输入量
        context.getCounter(GROUP_100, CODE_101).increment(1);
        if (partitioner == null || super.compress == null) {
            context.getCounter(GROUP_300, CODE_301).increment(1);
            return false;
        }
        //解压缩
        super.realValue = compress.decompress(value.toString());
        return true;
    }

    @Override
    public void map(String value, WrappedContext context) throws IOException, InterruptedException {
        if (StringUtils.isEmpty(value)) {
            context.getCounter(GROUP_200, CODE_201).increment(1);
            return;
        }
        String realValue = StringUtils.substringBeforeLast(value, "\t");
        String cnt = StringUtils.substringAfterLast(value, "\t");
        context.write(realValue, cnt + "\t" + dataType);
    }
}