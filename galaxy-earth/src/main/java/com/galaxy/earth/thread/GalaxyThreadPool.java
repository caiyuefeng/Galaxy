package com.galaxy.earth.thread;

import java.util.concurrent.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 自定义线程池
 * @date : 2019/3/14 10:11
 **/
public class GalaxyThreadPool {

    /**
     * 线程池实例
     */
    private ThreadPoolExecutor executor;

    /**
     * 单例模式初始化标志
     */
    private static volatile boolean INSTANCE_FLAG = false;

    private GalaxyThreadPool() {
        if (INSTANCE_FLAG) {
            throw new RuntimeException("不能创建更多的实例!");
        }
        INSTANCE_FLAG = true;
        executor = new ThreadPoolExecutor(3, 3, 10,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(5), new GalaxyThreadFactory());
    }

    private static class InnerTool {
        private static GalaxyThreadPool TOOL = new GalaxyThreadPool();
    }

    /**
     * 获取默认线程池实例
     *
     * @return 线程池
     */
    public static ThreadPoolExecutor getInstance() {
       return InnerTool.TOOL.executor;
    }

    public  static ThreadPoolExecutor newInstance(){
        return new ThreadPoolExecutor(3, 3, 10,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(5), new GalaxyThreadFactory());
    }

    private static class GalaxyThreadFactory implements ThreadFactory {
        /**
         * 线程序号
         */
        private int cnt = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Galaxy-Thread-Pool-" + cnt++ + ":");
        }
    }
}
