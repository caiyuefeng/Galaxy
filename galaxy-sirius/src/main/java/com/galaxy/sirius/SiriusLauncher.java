package com.galaxy.sirius;


import com.galaxy.earth.GalaxyLog;
import com.galaxy.sirius.annotation.Run;
import com.galaxy.sirius.classloader.LauncherClassLoader;
import com.galaxy.sirius.classloader.SyncClassLoader;

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
public class SiriusLauncher {

	private static final String SIGN = ";";

	public static void run(String[] args) throws ReflectiveOperationException {
		LauncherClassLoader loader = LauncherClassLoader.getInstance();
		String classPath = System.getProperty("galaxy.class.path");
		if (classPath != null) {
			for (String childPath : classPath.split(SIGN, -1)) {
				loader.load(childPath);
			}
		}
		SyncClassLoader syncClassLoader = SyncClassLoader.getInstance();
		syncClassLoader.sync();
		Class<?> clazz = syncClassLoader.getExecutorClass();
		if (clazz == null) {
			GalaxyLog.FILE_WARN("未找到执行入口,请用Run注解标识执行入口!");
			return;
		}
		// 将当前类加载器设置为同步类加载器，用于替换原始类加载器
		Thread.currentThread().setContextClassLoader(syncClassLoader);
		Class<?> cla = syncClassLoader.loadClass(clazz.getTypeName());
		for (Method method : cla.getDeclaredMethods()) {
			for (Annotation annotation : method.getDeclaredAnnotations()) {
				if (Run.class.getTypeName().equals(annotation.annotationType().getTypeName())) {
					method.invoke(cla.newInstance(), new Object[]{args});
				}
			}
		}
		// 反射 通过SyncExecutor.getInstance() 方法获取单例对象，
		// 调用close()方法关闭线程池
		Class<?> syncExecutorClass = syncClassLoader.loadClass("com.galaxy.sirius.sync.SyncExecutor");
		Method method = syncExecutorClass.getDeclaredMethod("getInstance");
		Object object = method.invoke(null);
		Method closeMethod = syncExecutorClass.getDeclaredMethod("close");
		closeMethod.invoke(object);
	}

	private static ClassLoader getSyncClassLoader(LauncherClassLoader loader)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException,
			InvocationTargetException {
		Class<?> clazz = loader.loadClass("com.galaxy.sirius.classloader.SyncClassLoader");
		Constructor constructor = clazz.getConstructor(Map.class);
		constructor.setAccessible(true);
		Object classLoader = constructor.newInstance(loader.getClassBuffer());
		Method method = clazz.getDeclaredMethod("sync");
		method.invoke(classLoader);
		return (ClassLoader) classLoader;
	}
}
