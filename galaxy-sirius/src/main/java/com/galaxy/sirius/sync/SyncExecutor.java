package com.galaxy.sirius.sync;

import com.galaxy.earth.GalaxyLog;
import com.galaxy.earth.thread.GalaxyThreadPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 22:43 2019/9/6
 *
 */
@SuppressWarnings("unused")
public class SyncExecutor {

  private ThreadPoolExecutor threadPool;

  private static volatile SyncExecutor INSTANCE = null;

  private SyncExecutor() {
    this.threadPool = GalaxyThreadPool.getInstance();
  }

  public static SyncExecutor getInstance() {
    if (INSTANCE == null) {
      synchronized (SyncExecutor.class) {
        if (INSTANCE == null) {
          INSTANCE = new SyncExecutor();
        }
      }
    }
    return INSTANCE;
  }

  public void executor(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) {
    threadPool.execute(new Executor(object, methodName, parameterTypes, parameterValues));
  }

  private static class Executor implements Runnable {

    /**
     * 代理对象实例
     */
    private Object object;

    /**
     * 代理方法名
     */
    private String methodName;

    /**
     * 代理方法参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 代理方法输入参数
     */
    private Object[] parameterValues;

    Executor(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) {
      this.object = object;
      this.methodName = methodName;
      this.parameterTypes = parameterTypes;
      this.parameterValues = parameterValues;
    }

    @Override
    public void run() {
      try {
        GalaxyLog.FILE_DEBUG("线程[{}]开始执行runSync方法!", Thread.currentThread().getName());
        Method method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        method.invoke(object, parameterValues);
        GalaxyLog.FILE_DEBUG("线程[{}]执行runSync方法完毕!", Thread.currentThread().getName());
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        GalaxyLog.FILE_ERROR("调用代理方法异常!", e);
      }
    }
  }
}
