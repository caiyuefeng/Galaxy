package com.galaxy.hadoop.file;

import com.galaxy.utils.PathUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.galaxy.base.ConstantChar.SLASH;
import static com.galaxy.base.ConstantChar.UNDERLINE;
import static com.galaxy.base.ConstantPath.RECORD;
import static com.galaxy.base.ConstantPath.SUCCESS;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 分区输出文件操作
 * 将输出文件名称中的分区信息移入对应分区
 * 输出文件名: *PS分区信息PE*.nb
 * @date : 2018/12/10 17:13
 **/
public class BuildPartitionOutputFile implements OutputFile {
    @Override
    public void take(FileSystem fs, Path srcPath, Path destPath, Configuration conf) throws IOException {
        List<Path> allFiles = new ArrayList<>();
        // 获取输出路径下所有文件
        PathUtils.getAllFile(fs, srcPath, allFiles);
        // 分区记录信息根路径
        Path recordPath = new Path(destPath, RECORD);
        PathUtils.makeDir(fs, recordPath);
        // 遍历输出文件 将文件移入对应分区
        for (Path path : allFiles) {
            String fileName = path.getName();
            if (SUCCESS.equals(fileName)) {
                continue;
            }
            String part = fileName.substring(fileName.indexOf("PS") + 2, fileName.indexOf("PE"));
            part = StringUtils.replace(part, UNDERLINE, SLASH);
            // 生成更新分区记录文件
            fs.createNewFile(new Path(recordPath, part));
            // 将文件移入分区
            Path partPath = new Path(destPath, part);
            PathUtils.makeDir(fs, partPath);
            fs.rename(path, partPath);
        }
        // 删除临时输出路径
        fs.delete(srcPath, true);
    }
}
