package com.galaxy.sun.utils;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/10 14:11
 **/
public class PathUtils {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(PathUtils.class);

    public static void getAllFile(FileSystem fs, Path basePath, List<Path> alllPaths) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(basePath);
        for (FileStatus status : fileStatuses) {
            if (status.isDirectory()) {
                getAllFile(fs, status.getPath(), alllPaths);
                continue;
            }
            if (status.isFile()) {
                alllPaths.add(status.getPath());
            }
        }
    }

    public static void makeDir(FileSystem fs, Path path) throws IOException {
        if (fs.exists(path)) {
            return;
        }
        if (!fs.mkdirs(path)) {
            LOG.error("创建路径:[" + path.toString() + "] 失败!l");
        }
    }

}
