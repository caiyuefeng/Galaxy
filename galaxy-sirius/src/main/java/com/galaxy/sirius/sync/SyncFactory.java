package com.galaxy.sirius.sync;

import com.galaxy.earth.FileUtils;
import com.galaxy.earth.GalaxyLog;
import com.galaxy.earth.exception.GalaxyException;
import com.galaxy.sirius.annotation.Sync;
import com.galaxy.sirius.classloader.SyncClassLoader;
import com.galaxy.sirius.utils.AsmUtils;
import com.galaxy.stone.Digit;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 同步工厂
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 21:03 2019/9/14
 */
@SuppressWarnings("unused")
public class SyncFactory {

	public static void syncAndSave(Class<?> clazz, String savePath) throws GalaxyException {
		byte[] bytes = transform(clazz);
		if (bytes == null) {
			return;
		}
		String parentName = clazz.getName().substring(0, clazz.getName().lastIndexOf("."));
		File parentDir = new File(savePath, parentName.replace(".", "/"));
		FileUtils.mkdir(parentDir);
		File classFile = new File(parentDir, clazz.getSimpleName() + ".class");
		try (FileOutputStream out = new FileOutputStream(classFile)) {
			GalaxyLog.FILE_DEBUG("开始保存类[{}],保存路径:[{}].", clazz.getName(), classFile.getAbsoluteFile().toString());
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			GalaxyLog.CONSOLE_FILE_ERROR(String.format("保存类%s文件失败!", clazz.getName()), e);
		}
	}

	private static byte[] transform(Class<?> clazz) {
		Method syncMethod = findSyncMethod(clazz);
		if (syncMethod == null) {
			return null;
		}
		ClassReader cr = new ClassReader(AsmUtils.getBytes(clazz));
		ClassWriter cw = new ClassWriter(Digit.ZERO.toInt());
		SyncMethodAdapter adapter = new SyncMethodAdapter(cw, syncMethod);
		cr.accept(adapter, Digit.ZERO.toInt());
		return cw.toByteArray();
	}

	public static Method findSyncMethod(Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {
			Annotation[] annotations = method.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().getTypeName().equals(Sync.class.getTypeName())) {
					return method;
				}
			}
		}
		GalaxyLog.CONSOLE_FILE_ERROR("类[{}]未找到同步方式,请使用Sync注解标识出同步方法!", clazz.getName());
		return null;
	}

	public static Class<?> sync(Class<?> clazz, SyncClassLoader loader) {
		byte[] bytes = transform(clazz);
		return loader.define(clazz.getName(), bytes);
	}

	public static Class<?> sync(Class<?> clazz) {
		byte[] bytes = transform(clazz);
		return bytes != null ? SyncClassLoader.getInstance().define(clazz.getName(), bytes) : null;
	}
}
