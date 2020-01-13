package com.galaxy.sun.hadoop.file;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2018/12/10 15:19
 **/
public interface OutputFile {

    /**
     * 输出文件操作方法接口
     *
     * @param fs       文件系统
     * @param srcPath  原始任务输出路径
     * @param destPath 目标输出路径
     * @param conf     任务配置信息
     * @throws IOException 1
     */
    void take(FileSystem fs, Path srcPath, Path destPath, Configuration conf) throws IOException;
}
