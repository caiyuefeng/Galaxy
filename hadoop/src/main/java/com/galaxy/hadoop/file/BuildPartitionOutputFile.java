package com.galaxy.hadoop.file;

import com.galaxy.utils.PathUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 分区输出文件操作
 * 将输出文件名称中的分区信息移入对应分区
 * @date : 2018/12/10 17:13
 **/
public class BuildPartitionOutputFile implements OutputFile {
    @Override
    public void take(FileSystem fs, Path srcPath, Path destPath, Configuration conf) throws IOException {
        List<Path> allFiles = new ArrayList<>();
        PathUtils.getAllFile(fs, srcPath, allFiles);
        Path recordPath = new Path(destPath, "record");
        PathUtils.makeDir(fs, recordPath);
        final StringBuilder builder = new StringBuilder();
        for (Path path : allFiles) {
            String fileName = StringUtils.substringAfterLast(path.toString(), "/");
            if ("_SUCCESS".equals(fileName)) {
                continue;
            }
            int startIndex = fileName.indexOf("PS") + 2;
            int endIndex = fileName.indexOf("PE");
            String part = fileName.substring(startIndex, endIndex);
            String[] dirs = part.split("_", -1);
            for (String val : dirs) {
                builder.append(val).append("/");
            }
            // 生成更新分区记录文件
            fs.createNewFile(new Path(recordPath, part));
            Path partPath = new Path(destPath, StringUtils.substringBeforeLast(builder.toString(), "/"));
            PathUtils.makeDir(fs, partPath);
            fs.rename(path, partPath);
        }
        fs.delete(srcPath, true);
    }
}
