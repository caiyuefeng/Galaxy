package com.galaxy.sirius.sync;

import com.galaxy.earth.ClassUtils;
import com.galaxy.sirius.annotation.Sync;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 同步类加载器
 * @Date : Create in 8:16 2019/10/1
 * @Modified By:
 */
public class SyncClassLoader extends ClassLoader {

    /**
     * 同步类缓存
     */
    private Map<String, Class<?>> classBuffer;

    private Map<String, Class<?>> classMap;

    public SyncClassLoader(Map<String, Class<?>> classMap) {
        classBuffer = new HashMap<>();
        this.classMap = classMap;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 不能写成 classBuffer.getOrDefault 否则会出现类找不到异常
        Class<?> clazz = classBuffer.get(name);
        if (clazz == null) {
            if(classMap.containsKey(name)){
                clazz =  define(name,ClassUtils.getBytes(classMap.get(name)));
            }else {
                clazz = super.findClass(name);
            }
        }
        return clazz;
    }

    Class<?> define(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }

    public void sync() {
        classMap.forEach((name, clazz) -> {
            boolean isSync = false;
            for (Method method : clazz.getDeclaredMethods()) {
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    if (annotation instanceof Sync) {
                        Class<?> cla = SyncFactory.sync(clazz, this);
                        if (cla != null) {
                            classBuffer.put(name, cla);
                            isSync = true;
                        }
                    }
                }
            }
            if (!isSync) {
                try {
                    classBuffer.put(name, define(clazz.getTypeName(), ClassUtils.getBytes(clazz)));
                }catch (LinkageError e){
                    classBuffer.put(name,clazz);
                }
            }
        });
    }
}