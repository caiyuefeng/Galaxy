package com.galaxy.asteroid.thread;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2019/3/13 17:05
 **/
public class QueueThread implements Runnable {

    /**
     * 前驱对象锁
     */
    private Object prev;

    /**
     * 自身对象锁
     */
    private Object self;

    private String value;

    public QueueThread(Object prev, Object self, String value) {
        this.prev = prev;
        this.self = self;
        this.value = value;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (prev) {
                synchronized (self) {
                    System.out.println(value);
                    self.notify();
                }
                try {
                    prev.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Object A = new Object();
        Object B = new Object();
        Object C = new Object();

        QueueThread thread1 = new QueueThread(C, A, "A");
        QueueThread thread2 = new QueueThread(A, B, "B");
        QueueThread thread3 = new QueueThread(B, C, "C");
        new Thread(thread1).start();
        Thread.sleep(10);
        new Thread(thread2).start();
        Thread.sleep(10);
        new Thread(thread3).start();
    }
}
