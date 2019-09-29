package com.galaxy.asteroid.test.thread;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 23:07 2019/3/13
 * @Modified By:
 */
public class SynchronizedObjectDemo implements Runnable {

    private ThreadObject object;

    public SynchronizedObjectDemo(ThreadObject object) {
        this.object = object;
    }

    @Override
    public void run() {
        int cnt = 0;
        while (cnt < 20) {
            int val = object.getValue();
            System.out.println(Thread.currentThread().getName()
                    + "-" + cnt + ":" + val);
            if(val%2!=0){
                break;
            }
            cnt++;
        }
    }

    private static class ThreadObject {

        private int cnt = 0;

        private Object object;

        public ThreadObject(Object object) {
            this.object = object;
        }

        public int getValue() {
            synchronized (object) {
                cnt++;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cnt++;
                return cnt;
            }
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }

    public static void main(String[] args) {
        ThreadObject object = new ThreadObject("测试");
        SynchronizedObjectDemo demo1 = new SynchronizedObjectDemo(object);
        ThreadObject object1 = new ThreadObject("测试1");
        object.setObject("重置");
        SynchronizedObjectDemo demo2 = new SynchronizedObjectDemo(object);
        new Thread(demo1).start();
        new Thread(demo2).start();
    }

}
