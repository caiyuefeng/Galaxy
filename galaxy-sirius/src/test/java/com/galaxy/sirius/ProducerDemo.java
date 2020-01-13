package com.galaxy.sirius;

import com.galaxy.sirius.annotation.Stage;
import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.enums.Role;

import java.io.Serializable;
import java.util.Random;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 22:13 2019/8/11
 *
 */
public class ProducerDemo {

    @Stage
    private static class Consumer implements Serializable {

        private int classInt;

        public Consumer(){
            classInt = new Random().nextInt()%10;
        }

        @Sync(role = Role.CONSUMER,num = 2)
        public void consumer(){
            System.out.println(String.format("消费者类%d开始运行",classInt));
            while (!ConsumerDemo.Producer.queue.isEmpty()){
                System.out.println(String.format("类%d:%s",classInt, ConsumerDemo.Producer.queue.poll()));
            }
        }

    }

}
