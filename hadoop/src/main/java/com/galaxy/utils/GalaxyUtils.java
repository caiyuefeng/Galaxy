package com.galaxy.utils;

import com.galaxy.common.galaxyclass.ClassUtils;
import com.galaxy.compress.DataCompress;
import com.galaxy.compress.DefaultDataCompress;
import com.galaxy.partitioner.DataPartitioner;
import com.galaxy.partitioner.DefaultPartitioner;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.galaxy.base.ConstantConfItem.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 通用工具类
 * @date : 2018/12/10 16:09
 **/
public class GalaxyUtils {

    /**
     * 获取任务文件输出策略
     *
     * @param conf 任务配置信息
     * @return 策略标志
     */
    public static int getHdFsOutputFileStrategy(Configuration conf) {
        return conf.getInt(OUTPUT_FILE_GET_STRATEGY_ITEM, 0);
    }

    /**
     * 获取任务文件输入策略
     *
     * @param conf 任务配置信息
     * @return 策略标志
     */
    public static int getHdFsInputFileStrategy(Configuration conf) {
        return conf.getInt(INPUT_FILE_GET_STRATEGY_ITEM, 0);
    }

    /**
     * 获取任务类型
     *
     * @param conf 配置信息
     * @return 任务类型标志
     */
    public static int getTaskType(Configuration conf) {
        return conf.getInt(TASK_TYPE, 0);
    }

    /**
     * 获取Map阶段分区器实例
     *
     * @param conf 任务配置信息
     * @param obj  分区器类型
     * @param <T>  分区器类参数类型
     * @return 分区器实例
     */
    public static <T> DataPartitioner<T> getMapPartitioner(Configuration conf, Class<T> obj) {
        String className = conf.get(MAP_PARTITION_CLASS_ITEM);
        return StringUtils.isEmpty(className) || getTaskType(conf) == 0 ? new DefaultPartitioner<T>() :
                (DataPartitioner<T>) ClassUtils.getClassInstance(className, obj);
    }

    public static <T> DataPartitioner<T> getReducePartitioner(Configuration conf, Class<T> obj) {
        String className = conf.get(REDUCE_PARTITION_CLASS_ITEM);
        return StringUtils.isEmpty(className) || getTaskType(conf) == 0 ? new DefaultPartitioner<T>() :
                (DataPartitioner<T>) ClassUtils.getClassInstance(className, obj);
    }

    /**
     * 获取数据压缩器实例
     *
     * @param conf 任务配置信息
     * @return 压缩器实例
     */
    public static DataCompress getCompressInstance(Configuration conf) {
        String className = conf.get(DATA_COMPRESS_ITEM);
        return StringUtils.isEmpty(className) ? DefaultDataCompress.getInstance() :
                ClassUtils.getClassInstance(className, DataCompress.class);
    }

    public static Map<String, String> loadProperties(Configuration conf) throws IOException {
        String itemStr = conf.get(MAP_REDUCE_PROPERTIES);
        if (StringUtils.isEmpty(itemStr)) {
            return null;
        }
        Map<String, String> buffer = new HashMap<>();
        String[] items = itemStr.split(";", -1);
        for (String item : items) {
            File file = new File(item);
            if (!file.exists()) {
                continue;
            }
            buffer.putAll(loadProperties(file));
        }
        return buffer;
    }

    private static Map<String, String> loadProperties(File file) throws IOException {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        Map<String, String> buffer = new HashMap<>();
        try {
            reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (StringUtils.isEmpty(line)) {
                    continue;
                }
                String[] kv = line.split("=", -1);
                if (kv.length != 2) {
                    continue;
                }
                buffer.put(kv[0], kv[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return buffer;
    }
}
