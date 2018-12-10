package com.galaxy.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.galaxy.base.ConstantConfItem.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/10 16:09
 **/
public class GalaxyUtils {


    public static int getHdFsOutputFileStrategy(Configuration conf) {
        return conf.getInt(OUTPUT_FILE_GET_STRATEGY_ITEM, 0);
    }

    public static int getHdFsInputFileStrategy(Configuration conf) {
        return conf.getInt(INPUT_FILE_GET_STRATEGY_ITEM, 0);
    }

    public static int getTaskType(Configuration conf) {
        return conf.getInt(TASK_TYPE, 0);
    }

    public static Map<String, String> loadProperties(Configuration conf) throws IOException {
        String itemStr = conf.get(MAP_REDUCE_PROPERTIES);
        if (StringUtils.isEmpty(itemStr)) {
            return null;
        }
        Map<String, String> buffer = new HashMap<>();
        String[] items = itemStr.split(";", -1);
        for (String item : items) {
            File file = new File(item);
            if (!file.exists()) {
                continue;
            }
            buffer.putAll(loadProperties(file));
        }
        return buffer;
    }

    private static Map<String, String> loadProperties(File file) throws IOException {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        Map<String, String> buffer = new HashMap<>();
        try {
            reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (StringUtils.isEmpty(line)) {
                    continue;
                }
                String[] kv = line.split("=", -1);
                if (kv.length != 2) {
                    continue;
                }
                buffer.put(kv[0], kv[1]);
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
        return buffer;
    }
}
