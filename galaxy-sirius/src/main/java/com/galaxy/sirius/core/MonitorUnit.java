package com.galaxy.sirius.core;

import com.galaxy.earth.enums.Digit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:40 2019/8/5
 * @Modified By:
 */
class MonitorUnit {

    /**
     * 生产者线程状态缓存
     */
    private static volatile ConcurrentHashMap<Integer, Map<Long, Boolean>> STAGE_RUNNING = new ConcurrentHashMap<>();

    /**
     * 消费者触发器
     */
    static volatile AtomicBoolean TRIGGER = new AtomicBoolean(false);

    /**
     * 获取指定阶段消费者线程运行状态
     *
     * @param stage 阶段唯一标识
     * @return 运行状态
     */
    static boolean producerIsRunning(int stage) {
        return TRIGGER.get() || (STAGE_RUNNING.containsKey(stage) ?
                STAGE_RUNNING.get(stage).values()
                        .stream().reduce((a, b) -> a | b)
                        .orElse(false) : true);
    }

    /**
     * 设置消费者运行状态
     *
     * @param stage  运行阶段
     * @param id     消费者唯一编号
     * @param status 运行状态
     */
    static void setProducerStatus(int stage, Long id, boolean status) {
        // 先设置触发器，防止在设置结束标志后消费者
        // 未获取到触发器正确状态导致失败
        if (status) {
            TRIGGER.compareAndSet(false, true);
        }
        if (STAGE_RUNNING.containsKey(stage)) {
            STAGE_RUNNING.get(stage).put(id, status);
        } else {
            Map<Long, Boolean> map = new HashMap<>(Digit.TWO.toInt());
            map.put(id, status);
            STAGE_RUNNING.put(stage, map);
        }
    }

}
