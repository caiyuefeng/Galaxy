package com.galaxy.asteroid.thread;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 测试 synchronized 作用于静态方法时加锁
 * 作用于静态方法时，对该类型的所有实例对象调用该方法都会产生互斥
 * @Date : Create in 22:03 2019/3/13
 * @Modified By:
 */
public class SynchronizedStaticMethodDemo implements Runnable {


    @SuppressWarnings("unused")
    private static class Field {
        private final Object lock = new Object();

        // 在静态代码区加锁
        static {
            synchronized (Field.class) {
                //...
            }
        }

        /**
         * 作用于方法
         */
        public synchronized void getValue() {
        }

        /**
         * 作用于代码块
         */
        public void setValue() {
            synchronized (this) {
                //...
            }
            synchronized (lock) {
                //...
            }
            synchronized (Field.class) {
                //...
            }
        }
    }


    private StaticMethodTool tool = new StaticMethodTool();

    private SynchronizedStaticMethodDemo(StaticMethodTool tool) {
        this.tool = tool;
    }

    @Override
    public void run() {
        int cnt = 0;
        while (cnt < 20) {
            int num = tool.getValue();
            System.out.println(Thread.currentThread().getName() +
                    "-" + cnt + ":" + num);
            if (num % 3 != 0) {
                break;
            }
            cnt++;
        }
    }


    private static class StaticMethodTool {
        private static int COUNT = 0;

        synchronized static int increase() {
            COUNT++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            COUNT++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            COUNT++;
            return COUNT;
        }

        public int getValue() {
            return increase();
        }
    }

    public static void main(String[] args) {

        // 对同一个对象
//        StaticMethodTool tool = new StaticMethodTool();
//        SynchronizedStaticMethodDemo demo1 = new SynchronizedStaticMethodDemo(tool);
//        SynchronizedStaticMethodDemo demo2 = new SynchronizedStaticMethodDemo(tool);
//        new Thread(demo1).start();
//        new Thread(demo2).start();

        SynchronizedStaticMethodDemo demo1 = new SynchronizedStaticMethodDemo(new StaticMethodTool());
        SynchronizedStaticMethodDemo demo2 = new SynchronizedStaticMethodDemo(new StaticMethodTool());
        new Thread(demo1).start();
        new Thread(demo2).start();

    }


}
