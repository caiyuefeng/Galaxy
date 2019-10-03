package com.galaxy.sirius;

import com.galaxy.earth.ClassUtils;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.util.Textifier;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:49 2019/9/6
 * @Modified By:
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AsmUtils {

    /**
     * 同步方法名
     */
    private static final String SYNC_METHOD_NAME = "runSync";

    /**
     * 打印类结构体
     *
     * @param clazz 待打印类
     */
    public static void printClassStruct(Class<?> clazz) {
        new ClassReader(ClassUtils.getBytes(clazz))
                .accept(new TraceClassVisitor(new ClassWriter(0), new Textifier(), new PrintWriter(System.out)), 0);
    }

    /**
     * 打印类结构体
     *
     * @param clazz 待打印类
     */
    public static void printClassStruct(byte[] clazz) {
        new ClassReader(clazz)
                .accept(new TraceClassVisitor(new ClassWriter(0), new Textifier(), new PrintWriter(System.out)), 0);
    }

    public static Class<?>[] getParameterTypes(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (SYNC_METHOD_NAME.equals(method.getName())) {
                return method.getParameterTypes();
            }
        }
        return new Class<?>[]{};
    }

}
