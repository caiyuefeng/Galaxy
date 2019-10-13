package com.galaxy.earth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 按行读取输入文件
     *
     * @param file 待读取文件
     * @return 读取行集
     * @throws IOException 1
     */
    public static List<String> listLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (Reader reader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }
        return lines;
    }

    /**
     * 按行输出数据到指定文件
     *
     * @param destFile 待输出文件
     * @param lines    待输出数据集
     * @throws IOException 1
     */
    public static void writeLines(File destFile, List<String> lines) throws IOException {
        try (Writer writer = new FileWriter(destFile);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            for (String line : lines) {
                bufferedWriter.write(line + "\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public static List<File> getAllFile(String rootPath) {
        List<File> files = new ArrayList<>();
        File root = new File(rootPath);
        if (root.exists()) {
            getAllFile(root, files);
        }
        return files;
    }

    private static void getAllFile(File rootDir, List<File> files) {
        if (rootDir.isFile()) {
            files.add(rootDir);
        }
        if (rootDir.isDirectory()) {
            File[] children = rootDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    getAllFile(child, files);
                }
            }
        }
    }
}
