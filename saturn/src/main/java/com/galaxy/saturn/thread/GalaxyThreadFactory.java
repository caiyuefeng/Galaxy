package com.galaxy.saturn.thread;

import java.util.concurrent.ThreadFactory;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/24 9:55
 **/
public class GalaxyThreadFactory implements ThreadFactory {
    private int index = 0;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, "Galaxy_Thread_Pool_" + index);
        index++;
        return thread;
    }
}
