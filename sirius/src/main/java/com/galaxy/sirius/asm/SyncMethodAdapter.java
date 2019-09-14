package com.galaxy.sirius.asm;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:01 2019/9/8
 * @Modified By:
 */
public class SyncMethodAdapter extends ClassVisitor {

    /**
     * 同步方法名
     */
    private static final String SYNC_METHOD_NAME = "runSync";

    /**
     * Java 线程类名
     */
    private static final String THREAD_CLASS_NAME = "java/lang/Thread";

    /**
     * 同步容器类名
     */
    private static final String SYNC_THREAD_CLASS_NAME = "com/galaxy/sirius/asm/SyncThread";

    /**
     * 同步方法实例
     */
    private Method method;

    /**
     * 同步方法标志:true表示找到同步方法；false表示未找到同步方法
     */
    private boolean sync = false;

    /**
     * 同步方法参数类型
     */
    private String type;

    /**
     * 同步方法泛型标识
     */
    private String generics;

    /**
     * 同步方法异常信息
     */
    private String[] exceptions;

    private ClassVisitor cv;

    SyncMethodAdapter(ClassVisitor cv, Method method) {
        super(ASM4, cv);
        this.method = method;
        this.cv = cv;
    }

    @Override
    public MethodVisitor visitMethod(int primary, String methodName, String type, String generics, String[] exceptions) {
        String realMethodName = methodName;
        if (methodName.equals(this.method.getName())) {
            realMethodName = SYNC_METHOD_NAME;
            this.type = type;
            this.generics = generics;
            this.exceptions = exceptions;
            sync = true;
        }
        return cv.visitMethod(primary, realMethodName, type, generics, exceptions);
    }

    @Override
    public void visitEnd() {
        // 需要增减同步方法
        if (sync) {
            MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, this.method.getName(), this.type, this.generics, this.exceptions);
            Class<?>[] parameterTypes = this.method.getParameterTypes();
            int parameterSize = parameterTypes.length;
            // Class<?> classes = new Class<?>[${parameterSize}];
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethods", "()[Ljava/lang/reflect/Method;", false);
            mv.visitMethodInsn(INVOKESTATIC, "com/galaxy/sirius/AsmUtils", "getParameterTypes", "([Ljava/lang/reflect/Method;)[Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, parameterSize + 1);
            // Object[] object = new Object[${parameterSize}];
            mv.visitLdcInsn(parameterSize);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitVarInsn(ASTORE, parameterSize + 2);
            // 初始化Class<?>[] Object[]
            for (int i = 0; i < parameterSize; i++) {
                String className = parameterTypes[i].getName();
                mv.visitVarInsn(ALOAD, parameterSize + 2);
                mv.visitLdcInsn(i);
                mv.visitVarInsn(getOpcode(className), i + 1);
                // 将基本类型转为包装类型
                if (AutoPackage.isBaseType(className)) {
                    mv.visitMethodInsn(INVOKESTATIC, AutoPackage.getPackageType(className),
                            "valueOf", AutoPackage.map.get(className), false);
                }
                mv.visitInsn(AASTORE);
            }

            // 新建 new SyncThread(${ParameterTypes})
            mv.visitTypeInsn(NEW, SYNC_THREAD_CLASS_NAME);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(SYNC_METHOD_NAME);
            mv.visitVarInsn(ALOAD, parameterSize + 1);
            mv.visitVarInsn(ALOAD, parameterSize + 2);
            mv.visitMethodInsn(INVOKESPECIAL, SYNC_THREAD_CLASS_NAME, "<init>", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)V", false);
            mv.visitVarInsn(ASTORE, 1);
            // 新建 new Thread(Runnable).start()
            mv.visitTypeInsn(NEW, THREAD_CLASS_NAME);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, THREAD_CLASS_NAME, "<init>", "(Ljava/lang/Runnable;)V", false);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, THREAD_CLASS_NAME, "start", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(16, parameterSize + 3);
            mv.visitEnd();
        }

        cv.visitEnd();
    }

    /**
     * 加载特定的字节码
     *
     * @param className 类型
     * @return 字节码
     */
    private int getOpcode(String className) {
        switch (className) {
            case "int":
                return ILOAD;
            case "byte":
                return ILOAD;
            case "short":
                return ILOAD;
            case "boolean":
                return ILOAD;
            case "character":
                return ILOAD;
            case "long":
                return LLOAD;
            case "float":
                return FLOAD;
            case "double":
                return DLOAD;
            default:
                return ALOAD;
        }
    }

    private static class AutoPackage {

        private static Map<String, String> map = new HashMap<>();

        static {
            map.put("int", "(I)Ljava/lang/Integer;");
            map.put("byte", "(B)Ljava/lang/Byte;");
            map.put("short", "(S)Ljava/lang/Short;");
            map.put("boolean", "(Z)Ljava/lang/Boolean;");
            map.put("char", "(C)Ljava/lang/Character;");
            map.put("long", "(L)Ljava/lang/Long;");
            map.put("float", "(F)Ljava/lang/Float;");
            map.put("double", "(D)Ljava/lang/Double;");
        }

        static boolean isBaseType(String className) {
            return map.containsKey(className);
        }

        static String getPackageType(String className) {
            switch (className) {
                case "int":
                    return "java/lang/Integer";
                case "byte":
                    return "java/lang/Byte";
                case "short":
                    return "java/lang/Short";
                case "boolean":
                    return "java/lang/Boolean";
                case "char":
                    return "java/lang/Character";
                case "long":
                    return "java/lang/Long";
                case "float":
                    return "java/lang/Float";
                case "double":
                    return "java/lang/Double";
                default:
                    return className;
            }
        }

    }
}
