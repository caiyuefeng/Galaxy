package com.galaxy.saturn.core;

import com.galaxy.saturn.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 文件合并输出方法
 * @date : 2018/12/24 9:53
 **/
public class MergeFileOutput implements Output {

    /**
     * 输出文件
     */
    private File file = new File("./output/merge.txt");

    /**
     * 输出数据缓存集
     */
    private List<String> lines = new ArrayList<>();

    private MergeFileOutput() {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException("create file exception !");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void take(String line) {
        lines.add(line);
    }

    @Override
    public void end() {
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
