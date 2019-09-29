package com.galaxy.asteroid.test.thread;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2019/3/14 11:36
 **/
public class ThreadLocalDemo {
    private ThreadLocal<Integer> localStr = new ThreadLocal<>();
    private static class ThreadDemo implements Runnable {
        private ThreadLocalDemo demo;
        private int step;
        private ThreadDemo(ThreadLocalDemo demo, int step) {
            this.demo = demo;
            this.step = step;
        }
        @Override
        public void run() {
            int cnt = 0;
            while (cnt < 2) {
                System.out.println(demo.localStr.get());
                demo.localStr.set(demo.localStr.get() + step);
                System.out.println(Thread.currentThread().getName() +
                        ":" + demo.localStr.get());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadLocalDemo localDemo = new ThreadLocalDemo();
        localDemo.localStr.set(1);
        System.out.println(localDemo.localStr.get());
        ThreadDemo demo1 = new ThreadDemo(localDemo,1);
        ThreadDemo demo2 = new ThreadDemo(localDemo,2);
        new Thread(demo1).start();
        new Thread(demo2).start();
        Thread.sleep(10);
        System.out.println(localDemo.localStr.get());
    }
}
