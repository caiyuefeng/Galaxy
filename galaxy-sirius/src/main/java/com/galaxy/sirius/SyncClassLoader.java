package com.galaxy.sirius;

import com.galaxy.earth.ClassUtils;
import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.sync.SyncFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 同步类加载器
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 8:16 2019/10/1
 */
public class SyncClassLoader extends ClassLoader {

	/**
	 * 同步类缓存。
	 */
	private Map<String, Class<?>> classBuffer;

	/**
	 * 原始类缓存。
	 */
	private Map<String, Class<?>> classMap;

	public SyncClassLoader(Map<String, Class<?>> classMap) {
		classBuffer = new HashMap<>();
		this.classMap = classMap;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// 不能写成 classBuffer.getOrDefault 否则会出现类找不到异常
		Class<?> clazz = classBuffer.get(name);
		if (clazz == null) {
			if (classMap.containsKey(name)) {
				clazz = define(name, ClassUtils.getBytes(classMap.get(name)));
			} else {
				clazz = super.findClass(name);
			}
		}
		return clazz;
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
		this.classMap.forEach((name, clazz) -> {
			boolean isSync = false;
			try {
				for (Method method : clazz.getDeclaredMethods()) {
					for (Annotation annotation : method.getDeclaredAnnotations()) {
						if (annotation instanceof Sync) {
							Class<?> cla = SyncFactory.sync(clazz, this);
							if (cla != null) {
								classBuffer.put(name, cla);
								isSync = true;
							}
						}
					}
				}
			} catch (LinkageError e) {
				// 不处理
			}

			if (!isSync) {
				try {
					classBuffer.put(name, define(clazz.getTypeName(), ClassUtils.getBytes(clazz)));
				} catch (LinkageError e) {
					classBuffer.put(name, clazz);
				}
			}
		});
		this.classMap.clear();
	}
}
