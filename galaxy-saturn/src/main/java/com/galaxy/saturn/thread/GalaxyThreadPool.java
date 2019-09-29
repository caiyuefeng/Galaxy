package com.galaxy.saturn.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/24 9:55
 **/
public class GalaxyThreadPool {
    public static ExecutorService getInstance(int threadNum) {
        return new ThreadPoolExecutor(threadNum + 1, threadNum + 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(), new GalaxyThreadFactory());
    }
}
