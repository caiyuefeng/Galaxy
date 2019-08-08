package com.galaxy.sirius;

import com.galaxy.earth.ClassUtils;
import com.galaxy.earth.thread.GalaxyThreadPool;
import com.galaxy.sirius.annotation.Stage;
import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.core.WorkUnit;
import com.galaxy.sirius.enums.Sign;

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

    private static SortedMap<Integer, List<WorkUnit>> workUnits = new TreeMap<>();

    private static ThreadPoolExecutor threadPool = GalaxyThreadPool.getInstance();

    static {
        executor();
    }

    public static void executor() {
        Set<Class<?>> classSet = ClassUtils.findUserClass("");
        for (Class<?> clazz : classSet) {
            Stage stage = clazz.getAnnotation(Stage.class);
            if (stage != null) {
                Method[] methods = clazz.getMethods();
                Map<Sign, String> methodMap = new HashMap<>();
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
        workUnits.values().forEach(workUnits1 ->
                workUnits1.forEach(workUnit ->
                        workUnit.start(threadPool)));

    }

    private static void submitWorkUnit(int stage, WorkUnit workUnit) {
        if (workUnits.containsKey(stage)) {
            workUnits.get(stage).add(workUnit);
        } else {
            List<WorkUnit> workUnitList = new ArrayList<>();
            workUnitList.add(workUnit);
            workUnits.put(stage, workUnitList);
        }
    }
}
