package com.galaxy.examples.sirius;

import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.enums.Role;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author 蔡月峰
 * @date 2020/1/21 14:34
 * @version 1.0
 **/
public class SyncConsumer {
  private int classInt;

  public SyncConsumer() {
    classInt = new Random().nextInt() % 10;
  }

  @Sync(role = Role.CONSUMER, num = 2)
  public void consumer() {
    System.out.println(String.format("消费者类%d开始运行", classInt));
    while (!SyncProducer.isStop) {
      System.out.println(String.format("消费者 %d 正在吃 %s 个葡萄!", classInt, SyncProducer.queue.poll()));
    }
  }
}
