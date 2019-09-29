package com.galaxy.sirius.asm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:43 2019/9/6
 * @Modified By:
 */
@SuppressWarnings("unused")
public class SyncThread implements Runnable {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(SyncThread.class);

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

    @SuppressWarnings("unused")
    public SyncThread(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) {
        this.object = object;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    @Override
    public void run() {
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            method.invoke(object, parameterValues);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("调用代理方法异常!", e);
            e.printStackTrace();
        }
    }
}
