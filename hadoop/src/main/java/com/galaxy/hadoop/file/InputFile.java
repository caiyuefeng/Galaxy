package com.galaxy.hadoop.file;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.List;

public interface InputFile {

    /**
     * 获取输入文件之前的操作
     *
     * @param fs   文件系统
     * @param conf 任务配置信息
     * @throws IOException 1
     */
    void begin(FileSystem fs, Configuration conf) throws IOException;

    /**
     * 获取 输入文件路径
     *
     * @param fs         文件系统
     * @param input      输入根路径
     * @param realInputs 真实输入文件路径
     * @return 获取标志
     * @throws IOException 1
     */
    boolean take(FileSystem fs, Path input, List<Path> realInputs) throws IOException;

    /**
     * 任务结束后 对输入路径的操作
     *
     * @param fs   文件系统
     * @param conf 任务配置信息
     * @throws IOException 1
     */
    void end(FileSystem fs, Configuration conf) throws IOException;

}
