package com.galaxy.examples.sirius;

import com.galaxy.boot.annotation.Run;

/**
 * @author 蔡月峰
 * @date 2020/1/21 14:34
 * @version 1.0
 **/
public class SyncExecutor {

  @Run
  public  void excutor(String[] args) throws InterruptedException {
    SyncProducer producer = new SyncProducer();
    producer.produce();
    SyncConsumer consumer = new SyncConsumer();
    consumer.consumer();
  }
}
