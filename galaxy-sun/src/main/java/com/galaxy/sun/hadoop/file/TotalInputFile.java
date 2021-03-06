package com.galaxy.sun.hadoop.file;

import com.galaxy.sun.utils.PathUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  输入文件全量获取
 * @date 2018/12/10 17:18
 **/
public class TotalInputFile implements InputFile {

    private TotalInputFile() {
    }

    private static class Inner {
        private static final TotalInputFile TOOL = new TotalInputFile();
    }

    public static TotalInputFile getInstance() {
        return Inner.TOOL;
    }

    @Override
    public void begin(FileSystem fs, Configuration conf) {
    }

    @Override
    public boolean take(FileSystem fs, Path input, List<Path> realInputs) throws IOException {
        PathUtils.getAllFile(fs, input, realInputs);
        return true;
    }

    @Override
    public void end(FileSystem fs, Configuration conf) {
    }
}
