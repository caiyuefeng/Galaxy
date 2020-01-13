package com.galaxy.sirius;

import com.galaxy.sirius.annotation.Stage;
import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.enums.Role;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 21:58 2019/8/14
 *
 */
public class ConsumerDemo {
    @Stage
    public static class Producer implements Serializable {
        public static volatile LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

        private int classInt;

        public Producer() {
            classInt = new Random().nextInt() % 10;
        }

        @Sync(num = 2,role = Role.PRODUCER)
        public void produce() throws InterruptedException {
            int index = 0;
            System.out.println(String.format("生产者类%d开始运行",classInt));
            Random random = new Random();
            while (index < 10) {
                queue.put(String.valueOf(Math.abs(random.nextInt() % 10)));
                index++;
            }
        }
    }


}
