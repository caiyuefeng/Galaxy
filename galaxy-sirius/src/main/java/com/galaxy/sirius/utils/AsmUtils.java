package com.galaxy.sirius.utils;

import com.galaxy.earth.GalaxyLog;
import com.galaxy.stone.Symbol;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.util.Textifier;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 22:49 2019/9/6
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AsmUtils {

	/**
	 * 同步方法名。
	 */
	private static final String SYNC_METHOD_NAME = "runSync";

	/**
	 * Class文件后缀名。
	 */
	private static final String CLASS_TAIL = "class";

	/**
	 * Jar文件URL协议头。
	 */
	private static final String JAR_FILE_URL_HEAD = "jar:file";

	/**
	 * 启动类加载器。
	 */
	private static final String LAUNCHER_CLASS_LOADER = "com.galaxy.sirius.classloader.LauncherClassLoader";

	/**
	 * 同步类加载器。
	 */
	private static final String SYNC_CLASS_LOADER = "com.galaxy.sirius.classloader.SyncClassLoader";

	/**
	 * 打印类结构体
	 *
	 * @param clazz 待打印类
	 */
	public static void printClassStruct(Class<?> clazz) {
		new ClassReader(getBytes(clazz))
				.accept(new TraceClassVisitor(new ClassWriter(0), new Textifier(), new PrintWriter(System.out)), 0);
	}

	/**
	 * 打印类结构体
	 *
	 * @param clazz 待打印类
	 */
	public static void printClassStruct(byte[] clazz) {
		new ClassReader(clazz)
				.accept(new TraceClassVisitor(new ClassWriter(0), new Textifier(), new PrintWriter(System.out)), 0);
	}

	public static Class<?>[] getParameterTypes(Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (SYNC_METHOD_NAME.equals(method.getName())) {
				return method.getParameterTypes();
			}
		}
		return new Class<?>[]{};
	}

	public static byte[] loadBytes(InputStream in) throws IOException {
		int len = in.available();
		byte[] bytes = new byte[len];
		int readLen = in.read(bytes);
		if (len != readLen) {
			GalaxyLog.FILE_WARN(String.format("读取字节数[%d]不等于输入字节数[%d]！", readLen, len));
		}
		return bytes;
	}

	/**
	 * 获取Class类的字节数组
	 *
	 * @param clazz class类名
	 * @return 字节数组
	 */
	public static byte[] getBytes(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("传入的类文件未NULL!！");
		}
		String classPath = "";
		Class<?> loaderClazz = clazz.getClassLoader().getClass();
		String classLoaderTypeName = loaderClazz.getTypeName();
		if (LAUNCHER_CLASS_LOADER.equals(classLoaderTypeName) || SYNC_CLASS_LOADER.equals(classLoaderTypeName)) {
			try {
				Method method = getMethod(loaderClazz, "getClassPath", String.class);
				classPath = (String) method.invoke(clazz.getClassLoader(), clazz.getName());
				// 读取Jar包中class文件
				if (classPath.startsWith(JAR_FILE_URL_HEAD)) {
					URL url = new URL(classPath);
					JarURLConnection connection = (JarURLConnection) url.openConnection();
					return loadBytes(connection.getJarFile().getInputStream(connection.getJarEntry()));
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				GalaxyLog.CONSOLE_FILE_ERROR(String.format("类 %s 反射方法 getClassPath 异常!", classLoaderTypeName), e);
			} catch (IOException e) {
				GalaxyLog.CONSOLE_FILE_ERROR(String.format("路径[%s]获取失败!", classPath), e);
			}
		} else {
			classPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
		}
		File file = new File(classPath);
		if (file.isDirectory()) {
			file = new File(file, clazz.getTypeName().replace(Symbol.DOT.getValue(), Symbol.SLASH.getValue()) + Symbol.DOT.getValue() + CLASS_TAIL);
		}
		try (InputStream in = new FileInputStream(file)) {
			return loadBytes(in);
		} catch (IOException e) {
			GalaxyLog.FILE_ERROR(String.format("读取文件[%s]异常", clazz.getName()), e);
			throw new RuntimeException(e);
		}
	}

	public static void saveClass(Class<?> clazz, File savePath) {
		File classFile = new File(savePath, clazz.getSimpleName() + ".class");
		try (FileOutputStream out = new FileOutputStream(classFile)) {
			GalaxyLog.FILE_DEBUG("开始保存类[{}],保存路径:[{}].", clazz.getName(), classFile.getAbsoluteFile().toString());
			out.write(getBytes(clazz));
			out.flush();
		} catch (IOException e) {
			GalaxyLog.FILE_ERROR("保存类[{}]文件失败!", clazz.getName(), e);
		}
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		Class<?> currentClass = clazz;
		while (currentClass != null) {
			for (Method method : currentClass.getDeclaredMethods()) {
				if (method.getName().equals(methodName) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
					return method;
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		throw new NoSuchMethodException(String.format("类 %s 没有 %s 方法", clazz.getTypeName(), methodName));
	}
}
