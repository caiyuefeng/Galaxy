package com.galaxy.hadoop.file;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/10 17:24
 **/
public class PartitionImportInputFile implements InputFile{
    @Override
    public void begin(FileSystem fs, Configuration conf) throws IOException {

    }

    @Override
    public boolean take(FileSystem fs, Path input, List<Path> realInputs) throws IOException {
        return false;
    }

    @Override
    public void end(FileSystem fs, Configuration conf) throws IOException {

    }
}
