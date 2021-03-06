package com.galaxy.sun.hadoop.mr;

import com.galaxy.earth.ClassUtils;
import com.galaxy.sun.hadoop.context.WrappedContext;
import com.galaxy.sun.hadoop.context.WrappedReducePartitionContext;
import com.galaxy.sun.partitioner.DataPartitioner;
import com.galaxy.sun.partitioner.DefaultPartitioner;
import com.galaxy.sun.utils.GalaxyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;

import java.io.IOException;

import static com.galaxy.sun.base.ConstantConfItem.MAP_PARTITION_CLASS_ITEM;
import static com.galaxy.sun.base.ConstantCounter.CODE_302;
import static com.galaxy.sun.base.ConstantCounter.GROUP_300;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2018/12/11 11:14
 **/
public abstract class BasePartitionReduce<KI, VI> extends BaseReduce<KI, VI, Text, Text> {

    private WrappedReducePartitionContext<KI, VI> context;

    /**
     * 分区器
     */
    public DataPartitioner<KI> partitioner;

    @Override
    protected  final void setup(Context context) {
        this.context = new WrappedReducePartitionContext<>(context);
        Configuration conf = context.getConfiguration();
        // 获取当前任务类型
        int taskType = GalaxyUtils.getTaskType(conf);
        // 获取Map阶段分区器
        String className = conf.get(MAP_PARTITION_CLASS_ITEM);
        partitioner = (StringUtils.isEmpty(className) || (taskType == 0)) ? new DefaultPartitioner<KI>()
                : ClassUtils.getInstance(className, DataPartitioner.class);
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
    public final void reduce(KI key, Iterable<VI> values, Context context) throws IOException, InterruptedException {
        if (take(key, values, this.context)) {
            String[] keys = partitioner.decode(key);
            reduce(keys[1], values, this.context);
        }
    }

    @Override
    public final boolean take(KI key, Iterable<VI> values, WrappedContext context) {
        if (partitioner == null) {
            context.getCounter(GROUP_300, CODE_302).increment(1);
            return false;
        }
        String[] keys = partitioner.decode(key);
        context.setDefaultPart(keys[0]);
        return true;
    }
}
