package com.galaxy.boot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 启动类加载器
 * @Date : Create in 8:14 2019/10/1
 * @Modified By:
 */
@SuppressWarnings("unused")
public class LauncherClassLoader extends ClassLoader {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(LauncherClassLoader.class);

    /**
     * 类缓存
     */
    private Map<String, Class<?>> classBuffer = new HashMap<>();

    /**
     * 类文件路径缓存
     */
    private Map<String, String> classPathBuffer = new HashMap<>();

    /**
     * 当前类加载路径
     */
    private String classPath;

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = classBuffer.get(name);
        if (clazz == null) {
            String childClassPath = name.replace(".", "/") + ".class";
            InputStream in = null;
            try {
                File classFile = new File(this.classPath, childClassPath);
                in = new FileInputStream(classFile);
                clazz = define(name, loadClass(in));
                classBuffer.put(name, clazz);
                classPathBuffer.put(name, classFile.getAbsolutePath());
            } catch (IOException e) {
                LOG.error(String.format("读取文件[%s]出现异常", name), e);
                e.fillInStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return clazz == null ? super.findClass(name) : clazz;
    }

    static LauncherClassLoader getInstance(String classPath) {
        LauncherClassLoader loader = new LauncherClassLoader();
        loader.load(classPath);
        return loader;
    }

    private Class<?> define(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }

    void load(String classPath) {
        this.classPath = classPath;
        load(new File(classPath), "");
    }

    private void load(File classFile, String qualifiedName) {
        if (classFile.isDirectory()) {
            File[] files = classFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    String currQualifiedName = isEmpty(qualifiedName) ? file.getName() : qualifiedName + "." + file.getName();
                    load(file, currQualifiedName);
                }
                return;
            }
            LOG.debug("路径[{}]没有子路径或文件", classFile.getAbsolutePath());
        }
        if (classFile.isFile() && classFile.getName().endsWith("class")) {
            String currQualifiedName = isEmpty(qualifiedName) ? classFile.getName() : qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            try (InputStream in = new FileInputStream(classFile)) {
                classBuffer.put(currQualifiedName, define(currQualifiedName, loadClass(in)));
                classPathBuffer.put(currQualifiedName, classFile.getAbsolutePath());
            } catch (IOException e) {
                LOG.error(String.format("读取文件[%s]出现异常", classFile.getAbsolutePath()), e);
                e.fillInStackTrace();
            }
        }
    }

    private byte[] loadClass(InputStream in) throws IOException {
        int len = in.available();
        byte[] bytes = new byte[len];
        int realLen = in.read(bytes);
        if (realLen < len) {
            LOG.debug("读取字节数:[{}]小于实际字节数[{}]", realLen, len);
        }
        return bytes;
    }

    private boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }

    Map<String, Class<?>> getClassBuffer() {
        return classBuffer;
    }

    /**
     * 获取类对应的Class文件路径
     * 只能获取该加载器加载的类文件路径，不能获取父级加载器的类文件路径
     *
     * @param qualifiedName 类名
     * @return 类文件路径
     */
    public String getClassPath(String qualifiedName) {
        return classPathBuffer.get(qualifiedName);
    }
}

