package com.galaxy.asteroid.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 20:28 2019/3/13
 * @Modified By:
 */
public class ThreadCreateDemo {
    /**
     * 这是继承Thread类实现线程机制
     */
    private static class ThreadDemo extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("这是继承Thread类实现多线程");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 这是实现Runnable接口实现线程机制
     */
    private static class RunnableDemo implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("这是实现Runnable接口实现多线程");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 这是实现Callable接口实现线程方式
     */
    private static class CallableDemo implements Callable<String> {
        @Override
        public String call() throws Exception {
            try {
                while (true) {
                    System.out.println("这是实现Callable接口实现多线程");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public static void main(String[] args) {
        // 直接调用start()方法运行
        new ThreadDemo().start();
        ExecutorService service = Executors.newSingleThreadExecutor();

        //传入Thread对象运行
        new Thread(new RunnableDemo()).start();
        //提交交至线程池
        service.submit(new RunnableDemo());

        // 提交线程池
        service.submit(new CallableDemo());
        // 用FutureTask类包装后使用Thread类执行
        new Thread(new FutureTask<>(new CallableDemo())).start();
        service.shutdown();
    }
}
