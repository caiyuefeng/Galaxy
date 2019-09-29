package com.galaxy.sun.hadoop.file;

import com.galaxy.asteroid.string.GalaxyStringUtils;
import com.galaxy.sun.base.ConstantConfItem;
import com.galaxy.sun.utils.PathUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.galaxy.sun.base.ConstantPath.STATUS_PATH;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 根据时间戳获取增量输入文件
 * @date : 2018/12/10 17:24
 **/
public class TimestampImportInputFile implements InputFile {

    /**
     * 日志句柄
     */
    private final static Logger LOG = LoggerFactory.getLogger(TimestampImportInputFile.class);

    /**
     * 上次成功运行的时间戳
     */
    private String lastTimestamp = "";

    /**
     * 本次读取文件的最新时间戳
     */
    private String newestTimestamp = "";

    /**
     * yyyyMMddHHmmss的正则
     */
    private static Pattern TIME_PATTERN = Pattern.compile("\\d{14}");

    @Override
    public void begin(FileSystem fs, Configuration conf) throws IOException {
        String jobName = conf.get(ConstantConfItem.JOB_NAME_ITEM);
        Path basePath = new Path(STATUS_PATH, jobName);
        // 获取上次成功运行的时间戳
        if (fs.exists(basePath)) {
            FileStatus[] fileStatuses = fs.listStatus(basePath);
            if (fileStatuses.length == 0 || !fileStatuses[0].isFile()) {
                return;
            }
            lastTimestamp = fileStatuses[0].getPath().getName();
        }
        newestTimestamp = lastTimestamp;
    }

    @Override
    public boolean take(FileSystem fs, Path input, List<Path> realInputs) throws IOException {
        // 获取输入路径下所有文件
        List<Path> totalPath = new ArrayList<>();
        PathUtils.getAllFile(fs, input, totalPath);
        for (Path path : totalPath) {
            // 从输入文件中获取yyyyMMddHHmmss格式的时间戳
            Matcher matcher = TIME_PATTERN.matcher(path.toString());
            if (matcher.find(0)) {
                String currentTimestamp = matcher.group(0);
                if (currentTimestamp.compareTo(lastTimestamp) > 0) {
                    realInputs.add(path);
                    newestTimestamp = GalaxyStringUtils.max(newestTimestamp, currentTimestamp);
                }
            }
        }
        if (realInputs.isEmpty()) {
            LOG.error("未获取到增量文件!上次成功运行的时间戳:" + lastTimestamp);
            return false;
        }
        return true;
    }

    @Override
    public void end(FileSystem fs, Configuration conf) throws IOException {
        Path basePath = new Path(STATUS_PATH, conf.get(ConstantConfItem.JOB_NAME_ITEM));
        fs.delete(basePath, true);
        if (fs.createNewFile(new Path(basePath, newestTimestamp))) {
            LOG.info("本次任务输入文件时间戳状态更新成功!时间戳为:" + newestTimestamp);
        }
    }
}
