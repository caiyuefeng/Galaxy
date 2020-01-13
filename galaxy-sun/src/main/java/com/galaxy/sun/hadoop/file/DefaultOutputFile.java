package com.galaxy.sun.hadoop.file;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

import static com.galaxy.sun.base.ConstantPath.SUCCESS;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  默认的输出文件操作
 * @date 2018/12/10 17:14
 **/
public class DefaultOutputFile implements OutputFile {

    private static class Inner {
        private static final DefaultOutputFile TOOL = new DefaultOutputFile();
    }

    private DefaultOutputFile() {
    }

    public static DefaultOutputFile getInstance() {
        return Inner.TOOL;
    }

    @Override
    public void take(FileSystem fs, Path srcPath, Path destPath, Configuration conf) throws IOException {
        fs.delete(new Path(srcPath, SUCCESS), true);
    }
}
