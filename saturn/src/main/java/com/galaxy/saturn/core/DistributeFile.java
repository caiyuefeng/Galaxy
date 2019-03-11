package com.galaxy.saturn.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 文件分发
 * 将输入文件集分发到不同机器上的不同线程中
 * @date : 2018/12/24 9:53
 **/
public class DistributeFile {

    public static Map<String, List<String>> take(List<String> paths, int currentMachineNo, int machineNum, int threadNum) {
        SortedMap<Integer, String> map = new TreeMap<>();
        for (String file : paths) {
            map.put(HashCodeBuilder.reflectionHashCode(file), file);
        }

        // 获取本机处理的文件
        int i = 1;
        List<String> handleFiless = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (i % machineNum == currentMachineNo) {
                handleFiless.add(entry.getValue());
            }
            i++;
        }

        Map<String, List<String>> groupMap = new HashMap<>(16);
        int averageNum = handleFiless.size() / threadNum;
        List<String> buffer = new ArrayList<>();
        int threadNo = 0;
        for (i = 0; i < handleFiless.size(); i++) {
            if (groupMap.containsKey(String.valueOf(threadNo - 1)) && threadNo == threadNum) {
                groupMap.get(String.valueOf(threadNo - 1)).add(handleFiless.get(i));
                continue;
            }
            buffer.add(handleFiless.get(i));
            if ((i - 1) % averageNum == 0) {
                groupMap.put(String.valueOf(threadNo), new ArrayList<String>(buffer));
                buffer.clear();
                threadNo++;
            }
        }
        return groupMap;
    }

}
