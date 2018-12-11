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
 * @Description: 合并分区的输出文件
 * 改操作的目标分区是已经存在的,并且已存在分区数据是待移入的分区数据的子集
 * @date : 2018/12/10 17:16
 **/
public class PartitionMergeOutputFile implements OutputFile {

    /**
     * 已更新分区集
     */
    private List<Path> alreadyUpdatePart = new ArrayList<>();

    @Override
    public void take(FileSystem fs, Path srcPath, Path destPath, Configuration conf) throws IOException {
        List<Path> allFiles = new ArrayList<>();
        PathUtils.getAllFile(fs, srcPath, allFiles);
        Path record = new Path(destPath, "record");
        PathUtils.makeDir(fs, record);
        for (Path path : allFiles) {
            String fileName = StringUtils.substringAfterLast(path.toString(), "/");
            if ("_SUCCESS".equals(fileName)) {
                continue;
            }
            int startIndex = fileName.indexOf("PS") + 2;
            int endIndex = fileName.indexOf("PE");
            String part = fileName.substring(startIndex, endIndex);
            part = StringUtils.replace(part, "_", "/");
            // 生成更新分区记录信息
            fs.createNewFile(new Path(record, part));
            Path partPath = new Path(destPath, StringUtils.substringBeforeLast(part, "/"));
            // 检查该分区本次是否已经更新过，如果没有则先删除该分区
            if (!alreadyUpdatePart.contains(partPath)) {
                fs.delete(partPath, true);
                alreadyUpdatePart.add(partPath);
            }
            PathUtils.makeDir(fs, partPath);
            fs.rename(path, partPath);
        }
        fs.delete(srcPath, true);
    }
}
