package com.galaxy.sirius.core;

import com.galaxy.sirius.annotation.Stage;
import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.enums.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 控制单元
 * @Date : Create in 21:39 2019/8/5
 * @Modified By:
 */
public class WorkUnit {

    private static final Logger LOG = LoggerFactory.getLogger(WorkUnit.class);

    private static AtomicLong ID_GENERATOR = new AtomicLong(0L);

    /**
     * 执行单元状态
     */
    private volatile ConcurrentHashMap<Long, Boolean> statusMap = new ConcurrentHashMap<>();

    /**
     * 执行阶段
     */
    private int stage;

    /**
     * 执行单元数
     */
    private int num;

    /**
     * 用户类
     */
    private Class<?> clazz;

    private Map<Sign, String> methodMap;

    private ThreadLocal<Object> localUserObject = new ThreadLocal<>();


    public WorkUnit(Class<?> clazz, Map<Sign, String> methodMap) {
        this.clazz = clazz;
        stage = clazz.getDeclaredAnnotation(Stage.class).num();
        try {
            num = clazz.getMethod(methodMap.get(Sign.RUN)).getAnnotation(Sync.class).num();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            Constructor constructor = this.clazz.getConstructor();
            constructor.setAccessible(true);
            localUserObject.set(constructor.newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        this.methodMap = methodMap;
    }

    public void start(ExecutorService service) {
        try {
            for (int i = 0; i < num; i++) {
                WorkPart workPart = newWorkPart();
                service.execute(workPart);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void register(long id, boolean status) {
        statusMap.put(id, status);
    }

    public boolean isRunning() {
        return statusMap.isEmpty() ? true : statusMap.reduce(1, (id, status) -> status, (a, b) -> a | b);
    }

    private WorkPart newWorkPart() throws IOException {
        long id = ID_GENERATOR.incrementAndGet();
        LOG.info(String.format("生成执行单元[%d]", id));
        WorkPart workPart = new WorkPart(id, deepCopyObject(localUserObject.get()), this.methodMap);
        workPart.setWorkUnit(this);
        return workPart;
    }

    private Object deepCopyObject(Object object) throws IOException {
        ByteArrayInputStream byteIn = null;
        ObjectInputStream in = null;
        try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
             ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput)) {
            objectOutput.writeObject(object);
            byteIn = new ByteArrayInputStream(byteOutput.toByteArray());
            in = new ObjectInputStream(byteIn);
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (byteIn != null) {
                byteIn.close();
            }
        }
        throw new RuntimeException(MessageFormat.format("[{0}] deepCopy exception!", object.getClass()));
    }

    public void close() {
        localUserObject.remove();
    }

    int getStage() {
        return stage;
    }
}
