package com.galaxy.asteroid.galaxyclass;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 自定义类加载器
 * @date : 2019/3/12 9:00
 **/
public class EncryptClassloader extends ClassLoader {
    /**
     * 加密工具
     */
    private EncryptAlgorithm encryptAlgorithm;
    /**
     * 类加载器缓存
     */
    private Map<String, byte[]> classBuffer;

    /**
     * JAR包路径
     */
    private String applicationPath;

    public EncryptClassloader(String applicationPath, EncryptAlgorithm algorithm) {
        super();
        classBuffer = new HashMap<>(16);
        this.applicationPath = applicationPath;
        encryptAlgorithm = algorithm;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 查找当前缓存是否已经加载过该类
        byte[] bytes;
        if (classBuffer.containsKey(name)) {
            bytes = classBuffer.get(name);
            return defineClass(name, bytes, 0, bytes.length);
        }

        // 从指定路径加载该类
        try {
            bytes = load(name);
            if (bytes != null) {
                classBuffer.put(name, bytes);
                return defineClass(name, bytes, 0, bytes.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 委托父加载类进行加载
        return super.findClass(name);
    }

    /**
     * 从指定路径读取Class文件 ,并返回字节数据
     *
     * @param name 类名
     * @return 字节数组
     * @throws IOException 1
     */
    private byte[] load(String name) throws IOException {
        InputStream in = null;
        try {
            String classFilePath = applicationPath + name.substring(name.lastIndexOf(".") + 1, name.length())
                    + ".class";
            File file = new File(classFilePath);
            if (!file.exists()) {
                return null;
            }
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            if (in.read(bytes) > 0) {
                return bytes;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return null;
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        EncryptClassloader classloader = new EncryptClassloader("D:\\workspace\\Galaxy\\Galaxy\\asteroid\\input\\", null);
        Class<?> cla1 = classloader.loadClass("com.galaxy.earth.date.DateUtils");
        Method method = cla1.getMethod("getDate");
        Object o = cla1.newInstance();
        System.out.println(method.invoke(o));
        Class<?> cla2 = classloader.loadClass("com.galaxy.earth.date.DateUtils");
        method = cla2.getMethod("getDate");
        o = cla2.newInstance();
        System.out.println(method.invoke(o));
        System.out.println(cla1 == cla2);
    }
}
