package com.galaxy.sun.hadoop.mr;

import com.galaxy.meteorolite.galaxyclass.ClassUtils;
import com.galaxy.sun.base.DataType;
import com.galaxy.sun.hadoop.context.WrappedContext;
import com.galaxy.sun.hadoop.context.WrappedReducePartitionContext;
import com.galaxy.sun.hadoop.context.WrappedReduceSecondarySortContext;
import com.galaxy.sun.hadoop.writable.DataTypeKey;
import com.galaxy.sun.partitioner.DataPartitioner;
import com.galaxy.sun.partitioner.DefaultPartitioner;
import com.galaxy.sun.utils.GalaxyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.galaxy.sun.base.ConstantConfItem.MAP_PARTITION_CLASS_ITEM;
import static com.galaxy.sun.base.ConstantCounter.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 二次排序 Reduce基类
 * @date : 2018/12/11 13:48
 **/
public abstract class BaseSecondarySortReduce extends BaseReduce<DataTypeKey, Text,Text,Text> {

    /**
     * Reduce上下文包装器
     */
    private WrappedReduceSecondarySortContext<DataTypeKey, Text> context;

    /**
     * Reduce聚合的真实值缓存
     */
    private List<Text> values = new ArrayList<>();

    /**
     * 分区器
     */
    public DataPartitioner<DataTypeKey> partitioner;

    @Override
    protected void setup(Context context) {
        this.context = new WrappedReduceSecondarySortContext<>(context);
        Configuration conf = context.getConfiguration();
        // 获取当前任务类型
        int taskType = GalaxyUtils.getTaskType(conf);
        // 获取Map阶段分区器
        String className = conf.get(MAP_PARTITION_CLASS_ITEM);
        partitioner = (StringUtils.isEmpty(className) || (taskType == 0)) ? new DefaultPartitioner<DataTypeKey>()
                : ClassUtils.getClassInstance(className, DataPartitioner.class);
        // 设置包装器内的Map分区标志
        if (!StringUtils.isEmpty(className) && taskType == 1) {
            this.context.setMapAlreadyPartition(true);
        }
        // 初始化包装器
        ((WrappedReducePartitionContext) this.context).init(GalaxyUtils.getReducePartitioner(conf, Text.class),
                GalaxyUtils.getCompressInstance(conf));
        setup(this.context);
    }

    @Override
    public final void reduce(DataTypeKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        if (take(key, values, this.context)) {
            String[] keys = partitioner.decode((key));
            this.values.clear();
            boolean check = false;
            for (Text value : values) {
                if (value == null || StringUtils.isEmpty(value.toString())) {
                    continue;
                }
                // 获取数据的新旧类型
                String dataType = StringUtils.substringAfterLast(value.toString(), "\t");
                // 如果第一个不是新数据则直接退出不处理该部分数据
                if (!check && !DataType.NEW.getValue().equals(dataType)) {
                    context.getCounter(GROUP_200, CODE_204).increment(1);
                    return;
                }
                this.values.add(new Text(StringUtils.substringBeforeLast(value.toString(), "\t")));
                check = true;
            }
            reduce(keys[1], this.values, this.context);
        }
    }

    @Override
    public boolean take(DataTypeKey key, Iterable<Text> values, WrappedContext context) {
        if (partitioner == null) {
            context.getCounter(GROUP_300, CODE_302).increment(1);
            return false;
        }
        String[] keys = partitioner.decode(key);
        context.setDefaultPart(keys[0]);
        return true;
    }
}
