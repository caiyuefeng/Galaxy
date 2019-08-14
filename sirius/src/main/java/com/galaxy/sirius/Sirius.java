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
        Set<Class<?>> classSet = dir == null ?
                ClassUtils.findUserClass(Symbol.EMPTY_STR.getValue()) :
                ClassUtils.findUserClass(dir);
        findUserClassAndSubmit(classSet);
        executeWorkUnit();
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
                    submitWorkUnit(stage.num(), new WorkUnit(clazz, methodMap));
                }
            }
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
