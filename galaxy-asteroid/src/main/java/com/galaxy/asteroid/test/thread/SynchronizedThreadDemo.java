package com.galaxy.asteroid.test.thread;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 测试 Synchronized 关键字对普通方法加锁
 * 对同一个对象的方法可以互斥，对不同的对象无影响
 * @Date : Create in 21:50 2019/3/13
 * @Modified By:
 */
public class SynchronizedThreadDemo implements Runnable {

    private MethodTool tool;

    private SynchronizedThreadDemo(MethodTool tool) {
        this.tool = tool;
    }

    @Override
    public void run() {
        int cnt = 0;
        while (true) {
            if (cnt == 5) {
                break;
            }
            System.out.println(Thread.currentThread().getName()
                    + ":" + tool.getValue());
            cnt++;
        }
    }

    private static class MethodTool {

        private String[] fields = new String[]{"苹果",
                "栗子", "梨子", "菠萝", "香蕉", "西瓜"};

        private int cnt = 0;

        public synchronized String getValue() {
            if (cnt >= fields.length) {
                cnt = 0;
            }
            return fields[cnt++];
        }

    }

    public static void main(String[] args) {
        // 测试一 相同对象
//        MethodTool tool = new MethodTool();
//        SynchronizedThreadDemo demo1 = new SynchronizedThreadDemo(tool);
//        SynchronizedThreadDemo demo2 = new SynchronizedThreadDemo(tool);
//        new Thread(demo1).start();
//        new Thread(demo2).start();

        // 测试二 不同对象
        SynchronizedThreadDemo demo1 = new SynchronizedThreadDemo(new MethodTool());
        SynchronizedThreadDemo demo2 = new SynchronizedThreadDemo(new MethodTool());
        new Thread(demo1).start();
        new Thread(demo2).start();

    }
}
