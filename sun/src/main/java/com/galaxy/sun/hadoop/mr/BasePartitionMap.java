package com.galaxy.sun.hadoop.mr;

import com.galaxy.sun.compress.DataCompress;
import com.galaxy.sun.hadoop.context.WrappedContext;
import com.galaxy.sun.hadoop.context.WrappedMapPartitionContext;
import com.galaxy.sun.partitioner.DataPartitioner;
import com.galaxy.sun.utils.GalaxyUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.galaxy.sun.base.ConstantCounter.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 分区任务Map基类
 * @date : 2018/12/11 10:36
 **/
public abstract class BasePartitionMap<KI, VI, KO> extends BaseMap<KI, VI, KO, Text> {

    /**
     * Map上下文包装器
     */
    public WrappedMapPartitionContext<KI,VI> context;

    /**
     * 分区器
     */
    public DataPartitioner<String> partitioner;

    /**
     * 压缩器
     */
    protected DataCompress compress;

    /**
     * 真实输入值
     */
    protected String realValue;

    @Override
    protected void setup(Context context) {
        this.context = new WrappedMapPartitionContext(context);
        setup(this.context);
    }

    @Override
    public void setup(WrappedContext context) {
        Configuration conf = context.getConfiguration();
        partitioner = GalaxyUtils.getMapPartitioner(conf, String.class);
        compress = GalaxyUtils.getCompressInstance(conf);
        ((WrappedMapPartitionContext) context).setPartMark(GalaxyUtils.getTaskType(conf) == 1);
    }

    @Override
    protected void map(KI key, VI value, Context context) throws IOException, InterruptedException {
        if (take(key, value, this.context)) {
            // 调用业务逻辑代码
            map(realValue, this.context);
        }
    }

    @Override
    public boolean take(KI key, VI value, WrappedContext context) {
        // 统计总输入量
        context.getCounter(GROUP_100, CODE_101).increment(1);
        if (partitioner == null || compress == null) {
            context.getCounter(GROUP_300, CODE_301).increment(1);
            return false;
        }
        //解压缩
        realValue = compress.decompress(value.toString());
        // 设置分区
        context.setDefaultPart(partitioner.encode(realValue));
        return true;
    }

    private static final String DEFAULT_INPUT_SPLIT_CLASS = "org.apache.hadoop.mapreduce.lib.input,taggedInputSplit";

    public String getCurrentFileName(Context context) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InputSplit inputSplit = context.getInputSplit();
        Class<? extends InputSplit> splitClass = inputSplit.getClass();
        if (splitClass.getName().equals(DEFAULT_INPUT_SPLIT_CLASS)) {
            Method method = splitClass.getDeclaredMethod("getInputSplit");
            method.setAccessible(true);
            FileSplit fileSplit = (FileSplit) method.invoke(inputSplit);
            return fileSplit.getPath().toString();
        }
        FileSplit fileSplit = (FileSplit) inputSplit;
        return fileSplit.getPath().toString();
    }
}
