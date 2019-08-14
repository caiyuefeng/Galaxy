package com.galaxy.sirius.core;

import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.enums.Role;
import com.galaxy.sirius.enums.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 执行单元
 * @Date : Create in 21:40 2019/8/5
 * @Modified By:
 */
public class WorkPart implements Runnable {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(WorkPart.class);

    /**
     * 控制单元
     */
    private WorkUnit workUnit;

    /**
     * 方法缓存
     */
    private Map<Sign, String> methodMap;

    /**
     * 执行单元唯一编码
     */
    private long id;

    /**
     * 用户类
     */
    private Class<?> clazz;

    /**
     * 用户类对象
     */
    private Object object;

    WorkPart(long id, Object object, Map<Sign, String> methodMap) {
        this.id = id;
        this.object = object;
        this.clazz = object.getClass();
        this.methodMap = methodMap;
    }

    @Override
    public void run() {
        LOG.info(String.format("执行单元[%d]开始执行...", id));
        workUnit.register(id, true);
        execute();
        workUnit.register(id, false);
        LOG.info(String.format("执行单元[%d]开始结束!", id));
    }

    private void execute() {
        try {
            Method method = clazz.getDeclaredMethod(methodMap.get(Sign.RUN));
            method.setAccessible(true);
            Role role = method.getAnnotation(Sync.class).role();
            int delay = 10;
            switch (role) {
                case COMMON:
                    method.invoke(object);
                    break;
                case CONSUMER:
                    // 至少执行一次
                    do {
                        MonitorUnit.TRIGGER.compareAndSet(true, false);
                        method.invoke(object);
                        LOG.info(String.format("执行单元[%d]阻塞[%d]毫秒.", id, delay));
                        Thread.sleep(delay);
                        delay += 15;
                    } while (MonitorUnit.producerIsRunning(workUnit.getStage()));
                    break;
                case PRODUCER:
                    // 设置生产者线程标志
                    MonitorUnit.setProducerStatus(workUnit.getStage(), id, true);
                    method.invoke(object);
                    MonitorUnit.setProducerStatus(workUnit.getStage(), id, false);
                    break;
                default:
                    throw new RuntimeException(MessageFormat.format("{0}未知角色", role));
            }
        } catch (InterruptedException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    void setWorkUnit(WorkUnit workUnit) {
        this.workUnit = workUnit;
    }

    long getId() {
        return id;
    }
}
