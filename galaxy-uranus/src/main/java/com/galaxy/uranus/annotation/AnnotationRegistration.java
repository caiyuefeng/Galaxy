package com.galaxy.uranus.annotation;

import com.galaxy.earth.FileUtils;
import com.galaxy.earth.GalaxyLog;
import com.galaxy.stone.ConfigurationHelp;
import com.galaxy.uranus.option.Option;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 注解中心
 * @Date : Create in 13:23 2019/12/29
 * @Modified By:
 */
public class AnnotationRegistration {

	/**
	 * 注解 - 类签名缓存
	 */
	private Map<Annotation, Class<?>> annotationClass;

	/**
	 * 单例模式
	 */
	private volatile static AnnotationRegistration instance = null;

	private AnnotationRegistration() {
		annotationClass = new HashMap<>();
	}

	public static AnnotationRegistration getInstance() {
		if (instance == null) {
			instance = new AnnotationRegistration();
			try {
				instance.load();
			} catch (IOException e) {
				GalaxyLog.FILE_ERROR("加载依赖包异常!", e);
			} catch (ClassNotFoundException e) {
				GalaxyLog.CONSOLE_FILE_ERROR("类型加载失败!", e);
			}
		}
		return instance;
	}

	/**
	 * 加载注解
	 */
	private void load() throws IOException, ClassNotFoundException {
		List<URL> urls = FileUtils.getAllJarFile(ConfigurationHelp.getGalaxyHome() + "/lib");
		for (URL url : urls) {
			switch (Protocol.valueOf(url.getProtocol())) {
				case file:
					loadAnnotationByFile(new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name())));
					break;
				case jar:
					loadAnnotationByJar(((JarURLConnection) url.openConnection()).getJarFile());
					break;
				default:
					throw new IllegalArgumentException(String.format("参数:%s目前不支持", url.getProtocol()));
			}
		}
	}

	/**
	 * 从Class文件中加载注解类
	 *
	 * @param file Class文件路径
	 */
	private void loadAnnotationByFile(File file) throws ClassNotFoundException {
		if (file.getName().endsWith(".jar")) {
			try {
				loadAnnotationByJar(new JarFile(file));
			} catch (IOException e) {
				GalaxyLog.FILE_ERROR("加载依赖包异常!", e);
			}
		}
	}

	/**
	 * 从Jar包中加载注解类
	 *
	 * @param jarFile Jar包路径
	 */
	private void loadAnnotationByJar(JarFile jarFile) throws ClassNotFoundException {
		Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
		while (jarEntryEnumeration.hasMoreElements()) {
			JarEntry entry = jarEntryEnumeration.nextElement();
			String name = entry.getName();
			name = formatName(name);
			if (name.endsWith(".class")) {
				int pos = name.lastIndexOf(".");
				try {
					loadAnnotation(Class.forName(name.substring(0, pos)));
				} catch (NoClassDefFoundError | ClassNotFoundException error) {
					GalaxyLog.FILE_ERROR("类加载失败", error);
				}
			}
		}
	}

	/**
	 * 格式化报名
	 * /pgn/pgt/cla.class -> pgn.pgt.cla.class
	 *
	 * @param name 原格式名
	 * @return 格式化后名称
	 */
	private String formatName(String name) {
		name = name.charAt(0) == '/' ? name.substring(1) : name;
		return name.replace('/', '.');
	}

	/**
	 * 加载类中的所有注解
	 *
	 * @param clazz 类
	 */
	private void loadAnnotation(Class<?> clazz) {
		Annotation[] annotations = clazz.getAnnotations();
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (isNotMetaAnnotation(annotation)) {
					annotationClass.put(annotation, clazz);
				}
			}
		}
	}

	private boolean isNotMetaAnnotation(Annotation annotation) {
		return !(annotation instanceof Retention) && !(annotation instanceof Target);
	}

	/**
	 * 获取指定注解类的对应所有类的迭代器
	 *
	 * @return 迭代器
	 */
	public Iterator<Map.Entry<Annotation, Class<?>>> iterator(Predicate<Annotation> predicate) {
		return annotationClass.entrySet().stream().filter(entry -> predicate.test(entry.getKey())).iterator();
	}

	/**
	 * 通过参数项获取 参数项对应的功能模块实例
	 *
	 * @param option      参数项
	 * @param moduleClass 功能模块类型
	 * @param <T>         目标类型
	 * @return 模块实例
	 */
	public <T> T getInstance(Option option, Class<?> moduleClass) {
		return null;
	}

	/**
	 * 通过参数名获取模块实例
	 *
	 * @param name        参数名
	 * @param moduleClass 模块类型签名
	 * @param <T>         目标类型
	 * @return 模块实例
	 */
	public <T> T getInstance(String name, Class<?> moduleClass) {
		return null;
	}

	public Map<Annotation, Class<?>> getAnnotationClass() {
		return annotationClass;
	}

	private enum Protocol {
		file, jar
	}
}
