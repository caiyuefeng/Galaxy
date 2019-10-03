package com.galaxy.sirius.sync;

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
     * 同步容器类名
     */
    private static final String SYNC_THREAD_CLASS_NAME = "com/galaxy/sirius/sync/SyncExecutor";

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
            // 获取方法参数所占SLOT长度
            Class<?>[] parameterTypes = this.method.getParameterTypes();
            int parameterSize = getParameterLength(parameterTypes);
            // 声明方法
            MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, this.method.getName(), this.type, this.generics, this.exceptions);
            // SyncExecutor executor=SyncExecutor.getInstance()
            mv.visitMethodInsn(INVOKESTATIC, "com/galaxy/sirius/sync/SyncExecutor", "getInstance", "()Lcom/galaxy/sirius/sync/SyncExecutor;", false);
            mv.visitVarInsn(ASTORE, parameterSize + 1);
            // Class<?>[] classes = AsmUtils.getParameterTypes(this.getClass());
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "com/galaxy/sirius/AsmUtils", "getParameterTypes", "(Ljava/lang/Class;)[Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, parameterSize + 2);
            // Object[] object = new Object[${parameterSize}];
            mv.visitLdcInsn(parameterTypes.length);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitVarInsn(ASTORE, parameterSize + 3);
            int index = 1;
            for (int i = 0; i < parameterTypes.length; i++) {
                String className = parameterTypes[i].getName();
                mv.visitVarInsn(ALOAD, parameterSize + 3);
                mv.visitLdcInsn(i);
                int opCode = getOpcode(className);
                mv.visitVarInsn(opCode, index);
                index += opCode == 22 || opCode == 24 ? 2 : 1;
                if (AutoPackage.isBaseType(className)) {
                    mv.visitMethodInsn(INVOKESTATIC, AutoPackage.getPackageType(className),
                            "valueOf", AutoPackage.map.get(className), false);
                }
                mv.visitInsn(AASTORE);
            }
            // executor.executor(this, "runSync", classes, objects)
            mv.visitVarInsn(ALOAD, parameterSize + 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(SYNC_METHOD_NAME);
            mv.visitVarInsn(ALOAD, parameterSize + 2);
            mv.visitVarInsn(ALOAD, parameterSize + 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, SYNC_THREAD_CLASS_NAME, "executor", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(5, parameterSize + 4);
            mv.visitEnd();
        }
        cv.visitEnd();
    }

    private int getParameterLength(Class<?>[] parameterTypes) {
        int len = 0;
        for (Class<?> clazz : parameterTypes) {
            if (clazz.getName().equals("long") || clazz.getName().equals("double")) {
                len += 2;
            } else {
                len += 1;
            }
        }
        return len;
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
            case "byte":
            case "short":
            case "boolean":
            case "char":
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
            map.put("long", "(J)Ljava/lang/Long;");
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
