package com.galaxy.hadoop.job;

import com.galaxy.hadoop.file.*;
import com.galaxy.hadoop.fileformat.DefaultFileOutputFormat;
import com.galaxy.hadoop.mr.BaseMap;
import com.galaxy.hadoop.mr.BaseReduce;
import com.galaxy.utils.GalaxyUtils;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.galaxy.base.ConstantConfItem.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/10 14:10
 **/
public class DefaultJob {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJob.class);

    private Class<? extends BaseMap> mapper = null;

    private Class<? extends BaseReduce> reducer = null;

    private Class<? extends WritableComparator> group = null;

    private Class<? extends WritableComparator> sort = null;

    private Class<? extends Partitioner> partitioner = null;

    private InputFile inputFile = null;

    private OutputFile outputFile = null;

    /**
     * 输入根路径
     */
    private Path inputPath;

    /**
     * 输出根路径
     */
    private Path outputPath;

    /**
     * 任务名
     */
    private String jobName;

    public DefaultJob() {
    }

    private boolean init(Configuration conf) throws ClassNotFoundException, IOException {

        // 获取输入路径
        String input = conf.get(INPUT_PATH_ITEM);
        if (StringUtils.isEmpty(input)) {
            LOG.error("未设置输入路径!请检查配置项:" + INPUT_PATH_ITEM);
            return false;
        }
        inputPath = new Path(input);

        // 获取输出路径
        String output = conf.get(OUTPUT_PATH_ITEM);
        if (StringUtils.isEmpty(output)) {
            LOG.error("未设置输入路径!请检查配置项:" + OUTPUT_PATH_ITEM);
            return false;
        }
        outputPath = new Path(output);

        // 设置Mapper 调用类
        String mapper = conf.get(MAP_CLASS_ITEM);
        this.mapper = this.mapper == null && !StringUtils.isEmpty(mapper) ? (Class<? extends BaseMap>) Class.forName(mapper) : this.mapper;
        if (mapper == null) {
            LOG.error("Mapper 调用类未设置!请检查配置项:" + MAP_CLASS_ITEM);
            return false;
        }

        // 设置 Reducer 调用类
        String reducer = conf.get(REDUCE_CLASS_ITEM);
        this.reducer = this.reducer == null && !StringUtils.isEmpty(reducer) ? (Class<? extends BaseReduce>) Class.forName(reducer) : this.reducer;

        // 获取分组起类名
        String group = conf.get(GROUP_COMPARATOR_ITEM);
        this.group = this.group == null && !StringUtils.isEmpty(group) ? (Class<? extends WritableComparator>) Class.forName(group) : this.group;

        // 获取排序器类名
        String sort = conf.get(SORT_COMPARATOR_ITEM);
        this.sort = this.sort == null && !StringUtils.isEmpty(sort) ? (Class<? extends WritableComparator>) Class.forName(sort) : this.sort;

        // 获取散列器类名
        String part = conf.get(PARTITIONER_ITEM);
        this.partitioner = this.partitioner == null && !StringUtils.isEmpty(part) ? (Class<? extends Partitioner>) Class.forName(part) : this.partitioner;

        // 初始化输出文件操作方法
        switch (GalaxyUtils.getHdFsOutputFileStrategy(conf)) {
            case 0:
                if (GalaxyUtils.getTaskType(conf) == 1) {
                    outputFile = new BuildPartitionOutputFile();
                    break;
                }
                outputFile = DefaultOutputFile.getInstance();
                break;
            case 1:
                outputFile = new BuildPartitionOutputFile();
                break;
            case 2:
                outputFile = new PartitionMergeOutputFile();
                break;
            default:
                outputFile = DefaultOutputFile.getInstance();
                break;
        }

        // 获取输入文件策略方法实例
        switch (GalaxyUtils.getHdFsInputFileStrategy(conf)) {
            case 0:
                inputFile = TotalInputFile.getInstance();
                break;
            case 1:
                inputFile = new TimestampImportInputFile();
                break;
            case 2:
                inputFile = new PartitionImportInputFile();
                break;
            default:
                inputFile = TotalInputFile.getInstance();
                break;
        }

        // 设置额外配置项
        final Gson gson = new Gson();
        conf.set(MAP_REDUCE_PROPERTIES, gson.toJson(GalaxyUtils.loadProperties(conf)));
        return true;
    }

    public boolean run(Configuration conf) throws IOException, ClassNotFoundException, InterruptedException {
        FileSystem fs = FileSystem.get(conf);
        conf.set(JOB_NAME_ITEM, jobName);
        if (!init(conf)) {
            return false;
        }
        Job job = Job.getInstance(conf);

        job.setJobName(jobName);
        job.setJarByClass(DefaultJob.class);

        job.setMapperClass(mapper);
        if (reducer != null) {
            job.setReducerClass(reducer);
        }

        Path tmpOutputPath = getRealOutputPath(conf, outputPath);
        fs.delete(tmpOutputPath, true);

        job.setOutputFormatClass(DefaultFileOutputFormat.class);
        DefaultFileOutputFormat.setOutputPath(job, tmpOutputPath);

        inputFile.begin(fs, conf);

        // 输入路径
        List<Path> paths = new ArrayList<>();
        if (!inputFile.take(fs, inputPath, paths) || paths.isEmpty()) {
            LOG.info("输入文件获取策略类:" + inputFile.getClass());
            LOG.info("输入路径为空!设置的输入根路径:" + inputPath.toString());
            return false;
        }

        if (!initMapReduceParameterClass(job)) {
            return false;
        }

        if (group != null) {
            job.setGroupingComparatorClass(group);
        }

        if (sort != null) {
            job.setSortComparatorClass(sort);
        }

        if (partitioner != null) {
            job.setPartitionerClass(partitioner);
        }

        job.setNumReduceTasks(conf.getInt(REDUCE_NUM, 300));

        LOG.info("任务:[" + jobName + "] 开始执行...");
        if (job.waitForCompletion(true)) {
            outputFile.take(fs, tmpOutputPath, outputPath, conf);
            if (GalaxyUtils.getHdFsOutputFileStrategy(conf) != 2) {
                inputFile.end(fs, conf);
            }
            LOG.info("任务:[" + jobName + "] 执行完成!");
        }
        return true;
    }

    private Path getRealOutputPath(Configuration conf, Path basePath) {
        switch (GalaxyUtils.getTaskType(conf)) {
            case 0:
                return new Path(basePath, "tmp");
            case 1:
                return basePath;
            default:
                return basePath;
        }
    }

    private void getSupperClass(Class<?> baseClass, List<Class<?>> classList) {
        if (classList.size() == 2) {
            return;
        }
        try {
            ParameterizedType type = (ParameterizedType) baseClass.getGenericSuperclass();
            Type[] types = type.getActualTypeArguments();
            if (types.length > 2) {
                for (int i = 2; i < types.length; i++) {
                    Class<?> instance = (Class<?>) types[i];
                    classList.add(instance);
                }
            }
        } catch (Exception e) {
            return;
        }
        getSupperClass(baseClass.getSuperclass(), classList);
    }

    /**
     * 初始化MapReduce输入输出参数类型
     *
     * @param job 任务
     * @return 初始化结果
     */
    private boolean initMapReduceParameterClass(Job job) {
        List<Class<?>> classes = new ArrayList<>();
        getSupperClass(mapper, classes);
        if (classes.size() != 2) {
            LOG.error("Mapper 输出类型初始化失败! 请检查Mapper调用类:" + mapper);
            return false;
        }
        job.setMapOutputKeyClass(classes.get(0));
        job.setMapOutputValueClass(classes.get(1));
        return true;
    }


}
