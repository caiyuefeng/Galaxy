package com.galaxy.earth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:41 2019/8/5
 * @Modified By:
 */
public class ClassUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ClassUtils.class);

    private static final String JAR_STR = "jar";

    private static final String EMPTY_STR = "";

    private static final String CLASS_TAIL = "class";

    public static void main(String[] args) {
        findUserClass("");
    }

    public static Set<Class<?>> findUserClass(String packageName) {
        File file = new File(ClassUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        Set<Class<?>> classSet = new HashSet<>();
        try {
            if (file.isFile() && file.getName().endsWith(JAR_STR)) {
                findUserClassByJar(file, classSet);
            }
            if (file.isDirectory()) {
                findUserClassByCompilePath(file, packageName, classSet);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classSet;
    }

    public static Set<Class<?>> findUserClass(File dir) {
        File[] children = dir.listFiles();
        Set<Class<?>> classSet = new HashSet<>();
        if (children == null) {
            return classSet;
        }
        try {
            for (File child : children) {
                if (child.getName().endsWith(JAR_STR)) {
                    findUserClassByJar(child, classSet);
                }
                if (child.isDirectory()) {
                    findUserClassByCompilePath(child, child.getName(), classSet);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classSet;
    }


    private static void findUserClassByJar(File dir, Set<Class<?>> classSet) throws IOException, ClassNotFoundException {
        if (dir.isFile()) {
            findUserClassByJar(new JarFile(dir), classSet);
        }
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    findUserClassByJar(child, classSet);
                }
            }
        }

    }

    private static void findUserClassByJar(JarFile jarFile, Set<Class<?>> classSet) throws ClassNotFoundException {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            name = name.startsWith("/") ? name.substring(1) : name;
            if (jarEntry.isDirectory() || !name.endsWith("class")) {
                continue;
            }
            name = name.substring(0, name.lastIndexOf("."));
            classSet.add(Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass(name));
        }
    }

    private static void findUserClassByCompilePath(File dir, String packageName, Set<Class<?>> classSet) throws ClassNotFoundException {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File file : children) {
            if (file.isDirectory()) {
                String childPackageName = EMPTY_STR.equals(packageName) ? file.getName() :
                        packageName + "." + file.getName();
                findUserClassByCompilePath(file, childPackageName, classSet);
                continue;
            }
            findUserClassByClassFile(file, packageName, classSet);
        }

    }


    private static void findUserClassByClassFile(File file, String packageName, Set<Class<?>> classSet) {
        if (file.isFile() && file.getName().endsWith(CLASS_TAIL)) {
            String name = file.getName();
            String className = packageName + "." + name.substring(0, name.lastIndexOf("."));
            try {
                classSet.add(Thread.currentThread()
                        .getContextClassLoader()
                        .loadClass(className));
            } catch (ClassNotFoundException e) {
                LOG.debug("[{}]Class文件加载失败!\n{}", file.getAbsoluteFile(), e.toString());
            }
        }
    }
}
