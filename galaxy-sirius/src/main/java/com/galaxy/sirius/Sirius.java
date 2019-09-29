package com.galaxy.sirius;

import com.galaxy.earth.ClassUtils;
import com.galaxy.earth.enums.Digit;
import com.galaxy.earth.enums.Symbol;
import com.galaxy.earth.thread.GalaxyThreadPool;
import com.galaxy.sirius.annotation.Stage;
import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.core.WorkUnit;
import com.galaxy.sirius.enums.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:35 2019/8/5
 * @Modified By:
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Sirius {

    private static final Logger LOG = LoggerFactory.getLogger(Sirius.class);

    /**
     * 工作单元缓存
     */
    private SortedMap<Integer, List<WorkUnit>> workUnitPool;

    /**
     * 运行线程池
     */
    private ThreadPoolExecutor threadPool;

    /**
     * 用户类路径
     */
    private File dir = null;

    static {
//        getInstance().executor();
    }

    private Sirius(File dir) {
        LOG.info("开始启动线程同步框架...");
        this.dir = dir;
        threadPool = GalaxyThreadPool.getInstance();
        workUnitPool = new TreeMap<>();
    }

    static Sirius getInstance(File dir) {
        return new Sirius(dir);
    }

    static Sirius getInstance() {
        return new Sirius(null);
    }

    void executor() {
        LOG.info("开始加载用户同步类...");
        Set<Class<?>> classSet = dir == null ?
                ClassUtils.findUserClass(Symbol.EMPTY_STR.getValue()) :
                ClassUtils.findUserClass(dir);
        LOG.info("用户同步类加载完毕!");
        LOG.info("开始注册同步方法...");
        findUserClassAndSubmit(classSet);
        LOG.info("同步方法完成!");
        LOG.info("开始运行同步方法...");
        executeWorkUnit();
        LOG.info("所有同步方法运行结束!同步框架推出!");
        threadPool.shutdown();
    }

    private void findUserClassAndSubmit(Set<Class<?>> classSet) {
        for (Class<?> clazz : classSet) {
            Stage stage = clazz.getAnnotation(Stage.class);
            if (stage != null) {
                Method[] methods = clazz.getMethods();
                Map<Sign, String> methodMap = new HashMap<>(Digit.ONE.toInt());
                for (Method method : methods) {
                    Sync sync = method.getAnnotation(Sync.class);
                    if (sync != null) {
                        methodMap.put(Sign.RUN, method.getName());
                    }
                }
                if (!methodMap.isEmpty()) {
                    LOG.info("开始注册同步类:{},同步方法:{}", clazz, methodMap.get(Sign.RUN));
                    submitWorkUnit(stage.num(), new WorkUnit(clazz, methodMap));
                }
            }
        }
        if (workUnitPool.isEmpty()) {
            LOG.warn("未找到用户同步类!");
            System.exit(1);
        }
    }

    private void submitWorkUnit(int stage, WorkUnit workUnit) {
        if (workUnitPool.containsKey(stage)) {
            workUnitPool.get(stage).add(workUnit);
        } else {
            List<WorkUnit> workUnitList = new ArrayList<>();
            workUnitList.add(workUnit);
            workUnitPool.put(stage, workUnitList);
        }
    }

    private void executeWorkUnit() {
        for (Map.Entry<Integer, List<WorkUnit>> entry : workUnitPool.entrySet()) {
            entry.getValue().forEach(workUnit -> workUnit.start(threadPool));
            int delay = 1000;
            boolean over = entry.getValue().stream()
                    .map(WorkUnit::isRunning)
                    .reduce((a, b) -> a | b).orElse(false);
            while (over) {
                LOG.info(String.format("当前阶段%d正在运行中...", entry.getKey()));
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                over = entry.getValue().stream()
                        .map(WorkUnit::isRunning)
                        .reduce((a, b) -> a | b).orElse(false);
                delay = +1000;
            }
        }
    }
}
