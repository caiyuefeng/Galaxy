package com.galaxy.asteroid.thread;

import java.util.Random;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 回调函数获取值
 * @date : 2019/3/13 17:31
 **/
public class CallBackFuncThread implements Runnable {

    private CallBackTool tool = new CallBackTool();

    private Random random = new Random();

    private DataStruct struct = new DataStruct();

    public CallBackFuncThread(CallBackTool tool) {
        this.tool = tool;
    }

    @Override
    public void run() {
        while (true) {
            int a = Math.abs(random.nextInt() % 10);
            int b = Math.abs(random.nextInt() % 10);
            int c = Math.abs(random.nextInt() % 10);
            tool.process(struct, a, b, c);
            System.out.println("a:" + a + "\tb:" + b + "\tc:" + c + "\t结果:" + struct.getSum());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        CallBackFuncThread funcThread = new CallBackFuncThread(new CallBackTool());
        new Thread(funcThread).start();

    }
}
