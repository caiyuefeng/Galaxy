package com.galaxy.sun.hadoop.file;

import com.galaxy.sun.base.ConstantChar;
import com.galaxy.sun.utils.PathUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.galaxy.sun.base.ConstantPath.RECORD;
import static com.galaxy.sun.base.ConstantPath.SUCCESS;

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
        // 获取输出路径先所有文件
        List<Path> allFiles = new ArrayList<>();
        PathUtils.getAllFile(fs, srcPath, allFiles);
        // 初始化分区记录根路径
        Path record = new Path(destPath, RECORD);
        fs.delete(record, true);
        PathUtils.makeDir(fs, record);
        // 将输出文件移入对应分区
        for (Path path : allFiles) {
            String fileName = path.getName();
            if (SUCCESS.equals(fileName)) {
                continue;
            }
            // 截取文件所属的分区
            String part = fileName.substring(fileName.indexOf("PS") + 2, fileName.indexOf("PE"));
            part = StringUtils.replace(part, ConstantChar.UNDERLINE, ConstantChar.SLASH);
            // 生成更新分区记录信息
            fs.createNewFile(new Path(record, part));
            Path partPath = new Path(destPath, part);
            // 检查该分区本次是否已经更新过，如果没有则先删除该分区
            if (!alreadyUpdatePart.contains(partPath)) {
                fs.delete(partPath, true);
                PathUtils.makeDir(fs,partPath);
                alreadyUpdatePart.add(partPath);
            }
            PathUtils.makeDir(fs, partPath);
            fs.rename(path, partPath);
        }
        fs.delete(srcPath, true);
    }
}
