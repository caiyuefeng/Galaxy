package com.galaxy.sun.base;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 配置项公共类
 * @date : 2018/12/10 14:08
 **/
public class ConstantConfItem {

    /**
     * 输出路径配置项
     */
    public static final String OUTPUT_PATH_ITEM = "mapreduce.outputdir";

    /**
     * 输入路径配置项
     */
    public static final String INPUT_PATH_ITEM = "mapreduce.inputdir";

    /**
     * Map调用类配置项
     */
    public static final String MAP_CLASS_ITEM = "mapreduce.map.class";

    /**
     * Reduce调用类配置项
     */
    public static final String REDUCE_CLASS_ITEM = "mapreduce.reduce.class";

    /**
     * Map阶段分区方法配置项
     */
    public static final String MAP_PARTITION_CLASS_ITEM = "mapreduce.map.partition";

    /**
     * Reduce阶段分区方法配置项
     */
    public static final String REDUCE_PARTITION_CLASS_ITEM = "mapreduce.reduce.partition";

    /**
     * 数据压缩方法配置项
     */
    public static final String DATA_COMPRESS_ITEM = "mapreduce.data.compress";

    /**
     * 任务输入策略配置项
     */
    public static final String INPUT_FILE_GET_STRATEGY_ITEM = "hdfs.input.file.strategy";

    /**
     * 任务输出策略配置项
     */
    public static final String OUTPUT_FILE_GET_STRATEGY_ITEM = "hdfs.output.file.strategy";

    /**
     * 任务名称配置项
     */
    public static final String JOB_NAME_ITEM = "mapreduce.job.name";

    /**
     * 分组方法配置项
     */
    public static final String GROUP_COMPARATOR_ITEM = "mapreduce.group.comparator.class";

    /**
     * 排序方法配置项
     */
    public static final String SORT_COMPARATOR_ITEM = "mapreduce.sort.comparator.class";

    /**
     * shuffe散列器配置项
     */
    public static final String PARTITIONER_ITEM = "mapreduce.partitioner.class";

    /**
     * 任务类型
     */
    public static final String TASK_TYPE = "task.type";

    /**
     * Reduce数目配置项
     */
    public static final String REDUCE_NUM = "mapreduce.reduce.num";

    /**
     * 额外配置项
     */
    public static final String MAP_REDUCE_PROPERTIES = "mapreduce.extends.properties";
}
