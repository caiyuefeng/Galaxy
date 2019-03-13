package com.galaxy.asteroid.thread;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:27 2019/3/13
 * @Modified By:
 */
public class PriorityThreadDemo implements Runnable {

    /**
     * 线程优先级
     */
    private int priority;

    public PriorityThreadDemo(int priority) {
        this.priority = priority;
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(priority);
        int cnt = 0;
        while (true) {
            System.out.println(Thread.currentThread().getName()+":"+cnt++);
            Thread.yield();

        }
    }

    public static void main(String[] args) {
        PriorityThreadDemo demo1 = new PriorityThreadDemo(Thread.MAX_PRIORITY);
        PriorityThreadDemo demo2 = new PriorityThreadDemo(Thread.MAX_PRIORITY);
        PriorityThreadDemo demo3 = new PriorityThreadDemo(Thread.MIN_PRIORITY);
//        ExecutorService service = Executors.newFixedThreadPool(2);
//        service.submit(demo3);
//        service.submit(demo2);
//        service.submit(demo1);
//        service.shutdown();
        new Thread(demo1).start();
        new Thread(demo2).start();
//        new Thread(demo3).start();
    }
}
