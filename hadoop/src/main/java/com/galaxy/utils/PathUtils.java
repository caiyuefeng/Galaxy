package com.galaxy.utils;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/10 14:11
 **/
public class PathUtils {
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
}
