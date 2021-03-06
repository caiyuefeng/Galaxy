package com.galaxy.sirius.classloader;

import com.galaxy.earth.GalaxyLog;
import com.galaxy.earth.exception.GalaxyException;
import com.galaxy.sirius.exception.UnSupportFileFormatException;
import com.galaxy.sirius.exception.UnSupportProtocolException;
import com.galaxy.stone.Symbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 启动类加载器
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 8:14 2019/10/1
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class LauncherClassLoader extends ClassLoader implements BaseClassLoader {

	public static LauncherClassLoader getInstance() {
		return new LauncherClassLoader();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> clazz = CLASS_BUFFER.get(name);
		if (clazz == null) {
			if (CLASS_BYTE_BUFFER.containsKey(name)) {
				clazz = define(name, CLASS_BYTE_BUFFER.get(name));
				CLASS_BUFFER.put(name, clazz);
			}
		}
		return clazz == null ? super.findClass(name) : clazz;
	}

	private Class<?> define(String name, byte[] bytes) {
		return defineClass(name, bytes, 0, bytes.length);
	}

	public void load(String classPath) {
		Map<URL, String> urlMap = new HashMap<>(16);
		try {
			loadClassUrl(new File(classPath), "", urlMap);
			loadClassBytes(urlMap);
		} catch (GalaxyException | IOException e) {
			GalaxyLog.CONSOLE_FILE_ERROR("类加载异常!", e);
		}
		loadClass(CLASS_BYTE_BUFFER);
	}

	/**
	 * 加载类字节码。
	 * 加载类时读取字节码后不直接加载，而是先将所有类字节码读取完毕后再进行类的加载，这样
	 * 在加载子类时其父类还未进行加载的情况下，通过loadClass(ClassName)进行加载时，当调用findClass(ClassName)进行
	 * 父类的查找，通过重写findClass()查找逻辑，先从classBuffer中查找，再从classByteBuffer中查找，再从父加载器查找
	 * 这样的查找逻辑可以正确的加载父类。从从而避免了类加载的顺序。
	 *
	 * @param handleFile    文件
	 * @param qualifiedName 类限定名
	 * @param urlMap        类路径=>限定名缓存
	 */
	private void loadClassUrl(File handleFile, String qualifiedName, Map<URL, String> urlMap)
			throws MalformedURLException, UnSupportFileFormatException {
		if (handleFile.isDirectory()) {
			File[] files = handleFile.listFiles();
			if (files != null) {
				for (File file : files) {
					String currQualifiedName = isEmpty(qualifiedName) ? file.getName() : qualifiedName + "." + file.getName();
					loadClassUrl(file, currQualifiedName, urlMap);
				}
				return;
			}
			GalaxyLog.FILE_DEBUG("路径[{}]没有子路径或文件", handleFile.getAbsolutePath());
		}
		// 加载jar包中的文件
		else if (handleFile.isFile()) {
			String suffix = getSuffixName(handleFile.getName()).toUpperCase();
			switch (Suffix.valueOf(suffix)) {
				case CLASS:
					String currQualifiedName = isEmpty(qualifiedName) ? handleFile.getName()
							: qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
					urlMap.put(handleFile.toURI().toURL(), qualifiedName);
					break;
				case JAR:
					urlMap.put(handleFile.toURI().toURL(), "");
					break;
				default:
					throw new UnSupportFileFormatException(String.format("目前不支持以%s结尾的文件格式!", suffix));
			}
		}
	}

	/**
	 * 加载ClassQualifiedName=>ClassUrl。
	 *
	 * @param urls 缓存
	 * @throws IOException                  异常
	 * @throws UnSupportFileFormatException 异常
	 * @throws UnSupportProtocolException   异常
	 */
	public void loadClassBytes(Map<URL, String> urls)
			throws IOException, UnSupportFileFormatException, UnSupportProtocolException {
		for (Map.Entry<URL, String> entry : urls.entrySet()) {
			switch (Protocol.valueOf(entry.getKey().getProtocol())) {
				case jar:
					loadClassBytes(((JarURLConnection) entry.getKey().openConnection()).getJarFile(), entry.getKey().getPath());
					break;
				case file:
					loadClassBytes(entry.getValue(),
							new File(URLDecoder.decode(entry.getKey().getFile(), StandardCharsets.UTF_8.name())));
					break;
				default:
					throw new UnSupportProtocolException("目前不支持Url协议 : " + entry.getKey().getProtocol());
			}
		}
	}

	private void loadClassBytes(String qualifiedName, File file) throws UnSupportFileFormatException {
		String fileName = file.getName();
		String suffix = getSuffixName(fileName);
		switch (Suffix.valueOf(getSuffixName(fileName).toUpperCase())) {
			case CLASS:
				loadClassBytesByClassFile(qualifiedName, file);
				break;
			case JAR:
				try {
					loadClassBytes(new JarFile(file), file.getAbsolutePath());
				} catch (IOException e) {
					GalaxyLog.FILE_ERROR("类加载异常! Jar包路径:" + file.getAbsolutePath(), e);
				}
				break;
			default:
				throw new UnSupportFileFormatException(String.format("目前不支持以%s结尾的文件格式!", suffix));
		}
	}

	private void loadClassBytesByClassFile(String qualifiedName, File classFile) {
		try (InputStream in = new FileInputStream(classFile)) {
			CLASS_BYTE_BUFFER.put(qualifiedName, loadBytes(in));
			CLASS_PATH_BUFFER.put(qualifiedName, classFile.getAbsolutePath());
		} catch (IOException e) {
			GalaxyLog.CONSOLE_FILE_ERROR(String.format("读取文件[%s]出现异常", classFile.getAbsolutePath()), e);
		}
	}

	/**
	 * 读取Jar包中的类字节码。
	 *
	 * @param jarFile   Jar包
	 * @param classPath 类文件路径
	 * @throws IOException 异常
	 */
	private void loadClassBytes(JarFile jarFile, String classPath) throws IOException {
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			JarEntry entry = enumeration.nextElement();
			String name = formatName(entry.getName());
			if (name.endsWith(Symbol.DOT.getValue() + Suffix.CLASS.name().toLowerCase())) {
				name = name.substring(0, name.lastIndexOf(Symbol.DOT.getValue()));
				CLASS_BYTE_BUFFER.put(name, loadBytes(jarFile.getInputStream(entry)));
				CLASS_PATH_BUFFER.put(name, "jar:file:" + classPath + "!/" + entry.getName());
			}
		}
	}

	private byte[] loadBytes(InputStream in) throws IOException {
		int len = in.available();
		byte[] bytes = new byte[len];
		int realLen = in.read(bytes);
		if (realLen < len) {
			GalaxyLog.FILE_WARN("读取字节数 {} 小于实际字节数 {}", realLen, len);
		}
		return bytes;
	}

	/**
	 * 通过限定名和字节码加载类。
	 *
	 * @param classByteBuffer 缓存
	 */
	public void loadClass(Map<String, byte[]> classByteBuffer) {
		classByteBuffer.forEach((name, bytes) -> {
			if (!CLASS_BUFFER.containsKey(name)) {
				try {
					CLASS_BUFFER.put(name, define(name, bytes));
				} catch (LinkageError e) {
					try {
						CLASS_BUFFER.put(name, super.loadClass(name));
					} catch (ClassNotFoundException | Error ex) {
						GalaxyLog.FILE_ERROR("类加载异常!", ex);
					}
				}
			}
		});
	}

	/**
	 * 格式化包名。
	 * 格式遵循以下形式：
	 * <code>
	 * /pgn/pgt/cla.class -> pgn.pgt.cla.class
	 * </code>
	 *
	 * @param name 原格式名
	 * @return 格式化后名称
	 */
	private String formatName(String name) {
		name = name.charAt(0) == '/' ? name.substring(1) : name;
		return name.replace('/', '.');
	}

	private String getSuffixName(String fileName) {
		return fileName.lastIndexOf('.') > 0 ? fileName.substring(fileName.lastIndexOf('.') + 1) : "";
	}

	private boolean isEmpty(String value) {
		return value == null || "".equals(value);
	}

	public Map<String, Class<?>> getClassBuffer() {
		return CLASS_BUFFER;
	}

	/**
	 * 获取类对应的Class文件路径
	 * 只能获取该加载器加载的类文件路径，不能获取父级加载器的类文件路径
	 *
	 * @param qualifiedName 类名
	 * @return 类文件路径
	 */
	public String getClassPath(String qualifiedName) {
		return CLASS_PATH_BUFFER.get(qualifiedName);
	}

	private enum Protocol {
		/**
		 * 文件协议
		 */
		file, jar
	}

	private enum Suffix {
		/**
		 * 文件尾
		 */
		JAR, CLASS
	}
}

