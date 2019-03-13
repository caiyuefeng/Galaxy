package com.galaxy.asteroid.thread;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:34 2019/3/13
 * @Modified By:
 */
public class SynchronizedFieldDemo implements Runnable {

    private FieldMethodTool tool;

    public SynchronizedFieldDemo(FieldMethodTool tool) {
        this.tool = tool;
    }

    @Override
    public void run() {
        int cnt = 0;
        while (cnt < 10) {
            int val = tool.getValueTwo();
            System.out.println(Thread.currentThread().getName()
                    + "-" + cnt + ":" + val);
//            System.out.println(Thread.currentThread().getName()
//                    + "-" + cnt + ":" + tool.getValueTwo());
            if (val % 2 != 0) {
                break;
            }
            cnt++;

        }
    }

    private static class FieldMethodTool {
        private int cnt = 0;

        public int getValueOne() {
            synchronized (this) {
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

        public int getValueTwo() {
            synchronized (FieldMethodTool.class) {
                cnt++;
                cnt++;
                return cnt;
            }
        }
    }

    public static void main(String[] args) {
        // 测试同一个对象
        FieldMethodTool tool = new FieldMethodTool();
        SynchronizedFieldDemo demo1 = new SynchronizedFieldDemo(tool);
        SynchronizedFieldDemo demo2 = new SynchronizedFieldDemo(tool);
        new Thread(demo1).start();
        new Thread(demo2).start();

        // 测试不同对象
//        SynchronizedFieldDemo demo1 = new SynchronizedFieldDemo(new FieldMethodTool());
//        SynchronizedFieldDemo demo2 = new SynchronizedFieldDemo(new FieldMethodTool());
//        new Thread(demo1).start();
//        new Thread(demo2).start();
    }

}
