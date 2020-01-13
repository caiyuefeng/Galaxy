package com.galaxy.saturn;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.*;
import java.util.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  文件工具类
 * @date 2018/12/24 9:55
 **/
public class Dispatcher {


    public static Map<String, List<String>> distributeFile(List<String> paths, int currentMachineNo, int machineNum, int threadNum) {
        SortedMap<Integer, String> map = new TreeMap<>();
        for (String file : paths) {
            map.put(HashCodeBuilder.reflectionHashCode(file), file);
        }

        // 获取本机处理的文件
        int i = 1;
        List<String> handleFiles = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (i % machineNum == currentMachineNo) {
                handleFiles.add(entry.getValue());
            }
            i++;
        }

        Map<String, List<String>> groupMap = new HashMap<>(16);
        int averageNum = handleFiles.size() / threadNum;
        List<String> buffer = new ArrayList<>();
        int threadNo = 0;
        for (i = 0; i < handleFiles.size(); i++) {
            if (groupMap.containsKey(String.valueOf(threadNo - 1)) && threadNo == threadNum) {
                groupMap.get(String.valueOf(threadNo - 1)).add(handleFiles.get(i));
                continue;
            }
            buffer.add(handleFiles.get(i));
            if ((i - 1) % averageNum == 0) {
                groupMap.put(String.valueOf(threadNo), new ArrayList<>(buffer));
                buffer.clear();
                threadNo++;
            }
        }
        return groupMap;
    }
}

