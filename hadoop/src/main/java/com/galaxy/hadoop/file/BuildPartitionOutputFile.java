package com.galaxy.hadoop.file;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/10 17:13
 **/
public class BuildPartitionOutputFile implements OutputFile{
    @Override
    public void take(FileSystem fs, Path srcPath, Path destPath, Configuration conf) throws IOException {

    }
}
