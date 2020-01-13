package com.galaxy.boot;

import com.galaxy.boot.annotation.Run;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 8:14 2019/10/1
 *
 */
public class GalaxyBoot {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String[] parameters = new String[args.length - 1];
        System.arraycopy(args, 1, parameters, 0, args.length - 1);
        run(args[0], parameters);
    }

    private static void run(String clazzName, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        LauncherClassLoader loader = LauncherClassLoader.getInstance();
        String classPath = System.getProperty("galaxy.class.path");
        for (String childPath : classPath.split(";", -1)) {
            loader.load(childPath);
        }
        ClassLoader syncLoader = getSyncClassLoader(loader);
        Thread.currentThread().setContextClassLoader(syncLoader);
        Class<?> cla = syncLoader.loadClass(clazzName);
        for (Method method : cla.getDeclaredMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if (annotation instanceof Run) {
                    method.invoke(cla.newInstance(), new Object[]{args});
                }
            }
        }
    }

    private static ClassLoader getSyncClassLoader(LauncherClassLoader loader) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = loader.loadClass("com.galaxy.sirius.sync.SyncClassLoader");
        Constructor constructor = clazz.getConstructor(Map.class);
        constructor.setAccessible(true);
        Object classLoader = constructor.newInstance(loader.getClassBuffer());
        Method method = clazz.getDeclaredMethod("sync");
        method.invoke(classLoader);
        return (ClassLoader) classLoader;
    }
}
