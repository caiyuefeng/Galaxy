package com.galaxy.asteroid;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 文件工具类
 * @date : 2018/12/24 9:55
 **/
public class FileUtils {

    /**
     * 按行读取输入文件
     *
     * @param file 待读取文件
     * @return 读取行集
     * @throws IOException 1
     */
    public static List<String> listLines(File file) throws IOException {
        Reader reader = null;
        BufferedReader bufferedReader = null;
        List<String> lines = new ArrayList<>();
        try {
            reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
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
        Writer writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter(destFile);
            bufferedWriter = new BufferedWriter(writer);
            for (String line : lines) {
                bufferedWriter.write(line + "\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }

}
