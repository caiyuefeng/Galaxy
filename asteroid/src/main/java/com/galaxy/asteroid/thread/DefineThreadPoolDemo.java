package com.galaxy.asteroid.thread;

import java.util.concurrent.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 自定义线程池
 * @date : 2019/3/14 10:32
 **/
public class DefineThreadPoolDemo {

    /**
     * 自定义的线程构造工厂
     */
    private static class ThreadFactoryDemo implements ThreadFactory {
        /**
         * 线程序号
         */
        private int cnt = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Pool-Demo-" + (++cnt) + ":");
        }
    }

    private static class ThreadDemo implements Runnable {
        private int cnt = 0;

        @Override
        public void run() {
            try {
                while (cnt < 5) {
                    System.out.println(Thread.currentThread().getName() + ":" + cnt++);
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static ThreadPoolExecutor getInstance() {
        // CorePoolSize：核心线程数，当前线程池可以运行的最大线程数
        // MaximumPoolSize：线程池最大可执行线程数，该参数是在线程缓存队列WorkQueue满时生效，当线程缓存
        // 队列满时，会新建额外线程运行线程
        // KeepAliveTime：额外线程销毁延迟时间,当线程在指定的延迟时间内不工作则销毁该线程
        // TimeUnit：延迟时间单位
        // WorkQueue：线程缓存队列
        // ThreadFactory：线程构造工厂
        return new ThreadPoolExecutor(1, 2,
                10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(2),
                new ThreadFactoryDemo());
    }

    public static void main(String[] args) {
        ExecutorService service = DefineThreadPoolDemo.getInstance();
        service.execute(new ThreadDemo());
        service.execute(new ThreadDemo());
        service.execute(new ThreadDemo());
        service.execute(new ThreadDemo());
        service.shutdown();
    }
}
