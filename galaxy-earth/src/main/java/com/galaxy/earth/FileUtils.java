package com.galaxy.earth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:12 2019/9/15
 * @Modified By:
 */
public class FileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    public static void mkdir(File dir) {
        File parent = dir.getParentFile();
        if (!parent.isDirectory()) {
            mkdir(parent);
        }
        if (dir.mkdir()) {
            LOG.debug("路径:[{}]创建成功!", dir);
        } else {
            LOG.error("路径:[{}]创建失败!", dir);
        }

    }
}
