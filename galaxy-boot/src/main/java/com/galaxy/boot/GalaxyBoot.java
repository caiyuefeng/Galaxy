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
 * @date Create in 8:14 2019/10/1
 */
public class GalaxyBoot {

	private static final String SIGN = ";";

	public static void main(String[] args) throws ReflectiveOperationException {
		run(args);
	}

	private static void run(String[] args) throws ReflectiveOperationException {
		LauncherClassLoader loader = LauncherClassLoader.getInstance();
		String classPath = System.getProperty("galaxy.class.path");
		if (classPath != null) {
			for (String childPath : classPath.split(SIGN, -1)) {
				loader.load(childPath);
			}
		}
		Class<?> clazz = loader.getExecutorClass();
		ClassLoader syncLoader = getSyncClassLoader(loader);
		// 将当前类加载器设置为同步类加载器，用于替换原始类加载器
		Thread.currentThread().setContextClassLoader(syncLoader);
		Class<?> cla = syncLoader.loadClass(clazz.getTypeName());
		for (Method method : cla.getDeclaredMethods()) {
			for (Annotation annotation : method.getDeclaredAnnotations()) {
				if (annotation instanceof Run) {
					method.invoke(cla.newInstance(), new Object[]{args});
				}
			}
		}
		// 反射 通过SyncExecutor.getInstance() 方法获取单例对象，
		// 调用close()方法关闭线程池
		Class<?> syncExecutorClass = syncLoader.loadClass("com.galaxy.sirius.sync.SyncExecutor");
		Method method = syncExecutorClass.getDeclaredMethod("getInstance");
		Object object = method.invoke(null);
		Method closeMethod = syncExecutorClass.getDeclaredMethod("close");
		closeMethod.invoke(object);
	}

	private static ClassLoader getSyncClassLoader(LauncherClassLoader loader)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException,
			InvocationTargetException {
		Class<?> clazz = loader.loadClass("com.galaxy.sirius.SyncClassLoader");
		Constructor constructor = clazz.getConstructor(Map.class);
		constructor.setAccessible(true);
		Object classLoader = constructor.newInstance(loader.getClassBuffer());
		Method method = clazz.getDeclaredMethod("sync");
		method.invoke(classLoader);
		return (ClassLoader) classLoader;
	}
}
