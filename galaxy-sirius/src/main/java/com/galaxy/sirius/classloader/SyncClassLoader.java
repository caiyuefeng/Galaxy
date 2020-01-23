package com.galaxy.sirius.classloader;

import com.galaxy.sirius.annotation.Run;
import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.sync.SyncFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步类加载器。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 8:16 2019/10/1
 */
public class SyncClassLoader extends ClassLoader implements BaseClassLoader {

	/**
	 * 同步类缓存。
	 */
	private Map<String, Class<?>> syncClassBuffer;

	/**
	 * 执行主类。
	 */
	private Class<?> executorClass;

	private SyncClassLoader() {
		syncClassBuffer = new HashMap<>();
	}

	public static SyncClassLoader getInstance() {
		return new SyncClassLoader();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// 不能写成 classBuffer.getOrDefault 否则会出现类找不到异常
		Class<?> clazz = syncClassBuffer.get(name);
		clazz = clazz == null ? CLASS_BUFFER.get(name) : clazz;
		return clazz == null ? super.findClass(name) : clazz;
	}

	/**
	 * 加载类字节码至内存
	 *
	 * @param name  类限定名
	 * @param bytes 类字节码
	 * @return 类
	 */
	public Class<?> define(String name, byte[] bytes) {
		return defineClass(name, bytes, 0, bytes.length);
	}

	/**
	 * 进行类的同步化处理。
	 * 扫描类加载器中类缓存，对所有使用{@link Sync} 注解的方法进行同步化处理，
	 * 处理方式为新增一个线程类
	 */
	public void sync() {
		List<Class<?>> className = BaseClassLoader.getClass(this::isSyncClass);
		// 移除当前类
		for (Class<?> update : className) {
			CLASS_BUFFER.remove(update.getName());
			this.syncClassBuffer.put(update.getName(), SyncFactory.sync(update, this));
		}
		loadExecutorClass();
	}

	/**
	 * 加载执行主类。
	 * 在类缓存中找到{@link Run}注解的方法，并使用本加载器加载该类，如果未用本类
	 * 重新加载，则后续找到的执行主类则会时其他类加载器进行加载的，此时同步化的类
	 * 未缓存至其他类加载器，会导致运行时未非同步化类。
	 */
	private void loadExecutorClass() {
		executorClass = BaseClassLoader.getFirstClass(this::isExecutorClass);
		String typeName = executorClass.getTypeName();
		Class<?> ex = define(executorClass.getTypeName(), CLASS_BYTE_BUFFER.get(typeName));
		syncClassBuffer.put(typeName, ex);
		executorClass = ex;
	}

	private boolean isSyncClass(Class<?> clazz) {
		try {
			for (Method method : clazz.getDeclaredMethods()) {
				for (Annotation annotation : method.getDeclaredAnnotations()) {
					if (annotation.annotationType().getTypeName().equals(Sync.class.getTypeName())) {
						return true;
					}
				}
			}
		} catch (LinkageError e) {
			// 不处理
		}
		return false;
	}

	/**
	 * 获取执行主类。
	 *
	 * @return 主类签名
	 */
	public Class<?> getExecutorClass() {
		return executorClass;
	}

	private boolean isExecutorClass(Class<?> clazz) {
		try {
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				if (isExecutorMethod(method)) {
					return true;
				}
			}
		} catch (Error e) {
			// 不处理
		}
		return false;
	}

	/**
	 * 检查传入的方法上的注解是否是{@link Run} 注解。
	 *
	 * @param method 方法实例
	 * @return 检查结果
	 */
	private boolean isExecutorMethod(Method method) {
		Annotation[] annotations = method.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			// 由于缓存中的类与Run的类加载器不一致，因此不可以直接使用 instanceOf 方法
			// 判定是否时同一个类型
			if (Run.class.getTypeName().equals(annotation.annotationType().getTypeName())) {
				return true;
			}
		}
		return false;
	}
}
