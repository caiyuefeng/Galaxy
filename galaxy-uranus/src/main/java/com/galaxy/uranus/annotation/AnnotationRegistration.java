package com.galaxy.uranus.annotation;

import com.galaxy.earth.FileUtils;
import com.galaxy.stone.ConfigurationHelp;
import com.galaxy.stone.SpecialConstantStr;
import com.galaxy.stone.Symbol;
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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

/**
 * 注解中心
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 13:23 2019/12/29
 */
public class AnnotationRegistration {

	/**
	 * 注解 - 类签名缓存
	 */
	private ConcurrentHashMap<Annotation, Class<?>> annotationClass;

	/**
	 * Jar包扫描线程池。
	 * 该线程池最大同时运行的线程为4个，线程结束后不保存直接销毁。
	 */
	private ExecutorService threadPool = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
		/**
		 * 线程编号.
		 */
		private int threadNo = 0;

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setName("Annotation_Load_Thread-" + threadNo);
			threadNo++;
			return thread;
		}
	});

	/**
	 * 单例模式
	 */
	private volatile static AnnotationRegistration instance = null;


	private AnnotationRegistration() {
		annotationClass = new ConcurrentHashMap<>();
	}

	public static AnnotationRegistration getInstance() throws IOException, ClassNotFoundException {
		if (instance == null) {
			instance = new AnnotationRegistration();
			instance.load();
		}
		return instance;
	}

	/**
	 * 加载注解
	 */
	private void load() throws IOException {
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
		threadPool.shutdown();
		while (!threadPool.isTerminated()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * 从Class文件中加载注解类
	 *
	 * @param file Class文件路径
	 */
	private void loadAnnotationByFile(File file) throws IOException {
		if (file.getName().endsWith(Symbol.DOT.getValue() + SpecialConstantStr.JAR_TAIL)) {
			try {
				loadAnnotationByJar(new JarFile(file));
			} catch (ZipException | Error e) {
				// 不处理
			}
		}
	}

	/**
	 * 从Jar包中加载注解类
	 *
	 * @param jarFile Jar包路径
	 */
	private void loadAnnotationByJar(JarFile jarFile) {
		threadPool.submit(new LoadThread(jarFile, annotationClass));
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
		/**
		 * 普通文件协议，jar文件URL协议
		 */
		file, jar
	}

	private static class LoadThread implements Runnable {

		private JarFile jarFile;

		private ConcurrentHashMap<Annotation, Class<?>> annotationClass;

		public LoadThread(JarFile jarFile, ConcurrentHashMap<Annotation, Class<?>> annotationClass) {
			this.jarFile = jarFile;
			this.annotationClass = annotationClass;
		}

		@Override
		public void run() {
			Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
			while (jarEntryEnumeration.hasMoreElements()) {
				JarEntry entry = jarEntryEnumeration.nextElement();
				String name = entry.getName();
				name = formatName(name);
				if (name.endsWith(Symbol.DOT.getValue() + SpecialConstantStr.CLASS_TAIL)) {
					int pos = name.lastIndexOf(Symbol.DOT.getValue());
					try {
						loadAnnotation(Class.forName(name.substring(0, pos)));
					} catch (ClassNotFoundException e) {
						// 依赖类未法相或不能加载 则放弃对应Jar包的加载
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
	}
}
