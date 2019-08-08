package com.galaxy.earth;

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

    private static final String JAR_STR = "jar";

    private static final String EMPTY_STR = "";

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
                findUserClassByCompile(file, packageName, classSet);
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

    private static void findUserClassByCompile(File file, String packageName, Set<Class<?>> classSet) throws ClassNotFoundException {
        if (file.isFile() && file.getName().endsWith("class")) {
            String name = file.getName();
            System.out.println(name);
            System.out.println(packageName);
            String className = packageName + "." + name.substring(0, name.lastIndexOf("."));
            classSet.add(Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass(className));
            return;
        }

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                packageName = file.getName().equals("classes") ? packageName :
                        packageName.equals(EMPTY_STR) ? file.getName() :
                                packageName + "." + file.getName();
                for (File child : children) {
                    findUserClassByCompile(child, packageName, classSet);
                }
            }
        }
    }
}
