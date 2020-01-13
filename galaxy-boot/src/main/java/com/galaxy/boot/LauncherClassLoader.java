package com.galaxy.boot;


import com.galaxy.stone.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author 蔡月峰
 * @version 1.0
 *  启动类加载器
 * @date Create in 8:14 2019/10/1
 *
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

    private Map<String, byte[]> classByteBuffer = new HashMap<>();

    /**
     * 当前类加载路径
     */
    private String classPath;

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = classBuffer.get(name);
        if (clazz == null) {
            if (classByteBuffer.containsKey(name)) {
                clazz = define(name, classByteBuffer.get(name));
                classBuffer.put(name, clazz);
            }
        }
        return clazz == null ? super.findClass(name) : clazz;
    }

    static LauncherClassLoader getInstance() {
        return new LauncherClassLoader();
    }

    private Class<?> define(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }

    void load(String classPath) {
        this.classPath = classPath;
        load(new File(classPath), "");
        classByteBuffer.forEach((name, bytes) -> {
            if (!classBuffer.containsKey(name)) {
                try {
                    classBuffer.put(name, define(name, bytes));
                } catch (LinkageError e) {
                    try {
                        classBuffer.put(name, super.loadClass(name));
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

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

        // 加载jar包中的文件
        if (classFile.isFile() && classFile.getName().endsWith("jar")) {
            try {
                JarFile jarFile = new JarFile(classFile);
                Enumeration<JarEntry> enumeration = jarFile.entries();
                while (enumeration.hasMoreElements()) {
                    JarEntry entry = enumeration.nextElement();
                    String name = entry.getName();
                    if (name.endsWith("class")) {
                        name = name.substring(0, name.lastIndexOf(Symbol.DOT.getValue())).replace(Symbol.SLASH.getValue(), Symbol.DOT.getValue());
                        String classPath = "jar:file:" + classFile.getAbsolutePath() + "!/" + entry.getName();
                        classByteBuffer.put(name, loadBytes(jarFile.getInputStream(entry)));
                        classPathBuffer.put(name, classPath);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 加载Class文件
        if (classFile.isFile() && classFile.getName().endsWith("class")) {
            String currQualifiedName = isEmpty(qualifiedName) ? classFile.getName() : qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            if (classBuffer.containsKey(currQualifiedName)) {
                return;
            }
            try (InputStream in = new FileInputStream(classFile)) {
                classByteBuffer.put(currQualifiedName, loadBytes(in));
                classPathBuffer.put(currQualifiedName, classFile.getAbsolutePath());
            } catch (IOException e) {
                LOG.error(String.format("读取文件[%s]出现异常", classFile.getAbsolutePath()), e);
            }
        }
    }

    private byte[] loadBytes(InputStream in) throws IOException {
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

