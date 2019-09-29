package com.galaxy.sun.base;

import org.apache.hadoop.fs.Path;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 公用路径类
 * @date : 2018/12/11 8:39
 **/
public class ConstantPath {

    /**
     * 工作空间
     */
    public static final Path BASE_PATH = new Path("/galaxy_common_path");

    /**
     * 状态位路径
     */
    public static final Path STATUS_PATH = new Path(BASE_PATH, "status");

    /**
     * 任务或工作流执行成功标志
     */
    public static final String SUCCESS_STATE = "success";

    /**
     * 任务或工作流执行正在执行标志
     */
    public static final String RUNNING_STATE = "running";

    /**
     *
     */
    public static final String SUCCESS = "_SUCCESS";


    /**
     * 分区信息记录路径
     */
    public static final String RECORD = "record";
}
