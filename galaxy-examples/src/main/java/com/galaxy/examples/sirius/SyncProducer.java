package com.galaxy.examples.sirius;

import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.enums.Role;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author 蔡月峰
 * @date 2020/1/21 14:33
 * @version 1.0
 **/
public class SyncProducer {
  public static volatile LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

  public static volatile boolean isStop = false;

  private int classInt;

  public SyncProducer() {
    classInt = new Random().nextInt() % 10;
  }

  @Sync(num = 2, role = Role.PRODUCER)
  public void produce() throws InterruptedException {
    int index = 0;
    System.out.println(String.format("生产者类%d开始运行", classInt));
    Random random = new Random();
    while (index < 10) {
      String num = String.valueOf(Math.abs(random.nextInt() % 10));
      queue.put(num);
      System.out.println(String.format("生产者%d 生产了%s 个葡萄!", classInt, num));
      index++;
    }
    isStop = true;
  }
}
