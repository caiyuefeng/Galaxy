package com.galaxy.earth;

import com.galaxy.stone.Digit;
import com.galaxy.stone.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:41 2019/8/5
 * @Modified By:
 */
@SuppressWarnings("unused")
public class ClassUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ClassUtils.class);

    private static final String JAR_STR = "jar";

    private static final String EMPTY_STR = "";

    private static final String CLASS_TAIL = "class";

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String className, Class<T> clazz) {
        try {
            Object o = clazz.newInstance();
            return (T) o;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<Class<?>> findUserClass(String packageName) {
        File file = new File(ClassUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        Set<Class<?>> classSet = new HashSet<>();
        try {
            if (file.isFile() && file.getName().endsWith(JAR_STR)) {
                findUserClassByJar(file, classSet);
            }
            if (file.isDirectory()) {
                findUserClassByCompilePath(file, packageName, classSet);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classSet;
    }

    public static Set<Class<?>> findUserClass(File dir) {
        File[] children = dir.listFiles();
        Set<Class<?>> classSet = new HashSet<>();
        if (children == null) {
            return classSet;
        }
        try {
            for (File child : children) {
                if (child.getName().endsWith(JAR_STR)) {
                    findUserClassByJar(child, classSet);
                }
                if (child.isDirectory()) {
                    findUserClassByCompilePath(child, child.getName(), classSet);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classSet;
    }

    private static void findUserClassByJar(File dir, Set<Class<?>> classSet) throws IOException, ClassNotFoundException {
        if (dir.isFile()) {
            findUserClassByJar(new JarFile(dir), classSet);
        }
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    findUserClassByJar(child, classSet);
                }
            }
        }

    }

    private static void findUserClassByJar(JarFile jarFile, Set<Class<?>> classSet) throws ClassNotFoundException {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            name = name.startsWith("/") ? name.substring(1) : name;
            if (jarEntry.isDirectory() || !name.endsWith(CLASS_TAIL)) {
                continue;
            }
            name = name.substring(0, name.lastIndexOf("."));
            classSet.add(Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass(name));
        }
    }

    private static void findUserClassByCompilePath(File dir, String packageName, Set<Class<?>> classSet) {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File file : children) {
            if (file.isDirectory()) {
                String childPackageName = EMPTY_STR.equals(packageName) ? file.getName() :
                        packageName + Symbol.DOT.getValue() + file.getName();
                findUserClassByCompilePath(file, childPackageName, classSet);
                continue;
            }
            findUserClassByClassFile(file, packageName, classSet);
        }

    }

    private static void findUserClassByClassFile(File file, String packageName, Set<Class<?>> classSet) {
        if (file.isFile() && file.getName().endsWith(CLASS_TAIL)) {
            String name = file.getName();
            String className = packageName + "." + name.substring(0, name.lastIndexOf("."));
            try {
                classSet.add(Thread.currentThread().getContextClassLoader()
                        .loadClass(className));
            } catch (ClassNotFoundException e) {
                LOG.debug("[{}]Class文件加载失败!\n{}", file.getAbsoluteFile(), e.toString());
            }
        }
    }

    /**
     * 获取Class类的字节数组
     *
     * @param clazz class类名
     * @return 字节数组
     */
    public static byte[] getBytes(Class<?> clazz) {
        String classPath = "";
        if (clazz.getClassLoader().getClass().getTypeName().equals("com.galaxy.boot.LauncherClassLoader")) {
            Class<?> loaderClazz = clazz.getClassLoader().getClass();
            try {
                Method method = loaderClazz.getDeclaredMethod("getClassPath", String.class);
                classPath = (String) method.invoke(clazz.getClassLoader(), clazz.getName());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            classPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        }
        File file = new File(classPath);
        try (InputStream in = new FileInputStream(file);
             BufferedInputStream buffer = new BufferedInputStream(in)) {
            int len = buffer.available();
            byte[] bytes = new byte[len];
            int readLen = buffer.read(bytes);
            if (len != readLen) {
                LOG.warn(String.format("读取字节数[%d]不等于输入字节数[%d]！", readLen, len));
            }
            return bytes;
        } catch (IOException e) {
            LOG.error(String.format("读取文件[%s]异常", clazz.getName()), e);
            throw new RuntimeException(e);
        }
    }

    public static void saveClass(Class<?> clazz, File savePath) {
        File classFile = new File(savePath, clazz.getSimpleName() + ".class");
        try (FileOutputStream out = new FileOutputStream(classFile)) {
            LOG.debug("开始保存类[{}],保存路径:[{}].", clazz.getName(), classFile.getAbsoluteFile().toString());
            out.write(ClassUtils.getBytes(clazz));
            out.flush();
        } catch (IOException e) {
            LOG.error("保存类[{}]文件失败!", clazz.getName(), e);
            e.printStackTrace();
        }
    }

    /**
     * 根据字节数组加载对应类
     *
     * @param className 类名
     * @param bytes     字节数组
     * @return class
     */
    public static Class<?> getClass(String className, byte[] bytes) {
        return new DefineClassLoader().defineClass(className, bytes);
    }

    private static class DefineClassLoader extends ClassLoader {
        Class<?> defineClass(String className, byte[] bytes) {
            return defineClass(className, bytes, Digit.ZERO.toInt(), bytes.length);
        }
    }
}
