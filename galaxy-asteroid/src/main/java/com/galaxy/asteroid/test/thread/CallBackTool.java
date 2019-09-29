package com.galaxy.asteroid.test.thread;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2019/3/13 17:34
 **/
public class CallBackTool {

    public void process(DataStruct dataStruct, int... num) {
        dataStruct.setSum(0);
        for (int v : num) {
            dataStruct.setSum(dataStruct.getSum() + v);
        }
    }

}
