package com.galaxy.sirius.asm;

import com.galaxy.earth.ClassUtils;
import com.galaxy.earth.enums.Digit;
import com.galaxy.sirius.annotation.Sync;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 同步工厂
 * @Date : Create in 21:03 2019/9/14
 * @Modified By:
 */
public class SyncFactory {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(SyncFactory.class);

    public static Class<?> sync(Class<?> clazz) {
        Method syncMethod = null;
        for (Method method : clazz.getDeclaredMethods()) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Sync) {
                    syncMethod = method;
                    break;
                }
            }
        }
        if (syncMethod == null) {
            LOG.warn("类[{}]未找到同步方式,请使用Sync注解标识出同步方法!", clazz.getName());
            return null;
        }
        ClassReader cr = new ClassReader(ClassUtils.getBytes(clazz));
        ClassWriter cw = new ClassWriter(Digit.ZERO.toInt());
        SyncMethodAdapter adapter = new SyncMethodAdapter(cw, syncMethod);
        cr.accept(adapter, Digit.ZERO.toInt());
        return ClassUtils.getClass(clazz.getName(), cw.toByteArray());
    }

}
