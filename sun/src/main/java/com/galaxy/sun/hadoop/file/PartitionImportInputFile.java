package com.galaxy.sun.hadoop.file;

import com.galaxy.sun.base.FileNameType;
import com.galaxy.sun.base.ConstantChar;
import com.galaxy.sun.utils.PathUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.galaxy.sun.base.ConstantPath.RECORD;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 分区文件增量获取策略
 * 根据分区记录信息获取存在更新的分区下的所有文件
 * @date : 2018/12/10 17:24
 **/
public class PartitionImportInputFile implements InputFile {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(PartitionImportInputFile.class);

    /**
     * 输入路径集
     */
    private List<Path> inputs = new ArrayList<>();

    /**
     * 分区记录路径
     */
    private Path record;

    @Override
    public void begin(FileSystem fs, Configuration conf) {
    }

    @Override
    public boolean take(FileSystem fs, Path input, List<Path> realInputs) throws IOException {
        record = new Path(input, RECORD);
        if (!fs.exists(record)) {
            LOG.error("无更新分区记录信息!分区路径:[" + record.toString() + "]");
            return false;
        }
        FileStatus[] fileStatuses = fs.listStatus(record);
        for (FileStatus fileStatus : fileStatuses) {
            // 获取分区信息
            String name = fileStatus.getPath().getName();
            name = StringUtils.replace(name, ConstantChar.UNDERLINE, ConstantChar.SLASH);
            // 将分区信息中最后的IMPORT子分区去除
            Path inputPath = new Path(input, StringUtils.substringBeforeLast(name, ConstantChar.SLASH));
            inputs.add(inputPath);
            // 获取分区下所有文件，包括新增数据和全量数据
            PathUtils.getAllFile(fs, inputPath, realInputs);
        }
        return true;
    }

    @Override
    public void end(FileSystem fs, Configuration conf) throws IOException {
        for (Path path : inputs) {
            // 初始化全量路径
            Path totalPath = new Path(path, FileNameType.TOTAL.getValue());
            PathUtils.makeDir(fs, totalPath);
            // 初始化增量路径
            Path importPath = new Path(path, FileNameType.IMPORT.getValue());
            // 将新增路径数据移入全量数据
            FileStatus[] fileStatuses = fs.listStatus(importPath);
            for (FileStatus fileStatus : fileStatuses) {
                String name = fileStatus.getPath().getName();
                name = StringUtils.replace(name, FileNameType.IMPORT.getValue(), FileNameType.TOTAL.getValue());
                fs.rename(fileStatus.getPath(), new Path(totalPath, name));
            }
        }
        // 删除更新分区记录
        fs.delete(record, true);
    }
}
