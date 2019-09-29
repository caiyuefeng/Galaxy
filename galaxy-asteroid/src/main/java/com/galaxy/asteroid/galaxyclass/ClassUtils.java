package com.galaxy.asteroid.galaxyclass;

import com.galaxy.earth.GalaxyStringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/10 16:42
 **/
public class ClassUtils {

    public static <T> T getClassInstance(String className, Class<T> destObj) {
        if (className != null && !"".equals(className)) {
            try {
                Class<?> classType = Class.forName(className);
                if (classType.equals(destObj)) {
                    Constructor<?> constructor = classType.getConstructor();
                    constructor.setAccessible(true);
                    return (T) constructor.newInstance();
                }
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        GalaxyStringUtils utils = getClassInstance("com.galaxy.earth.GalaxyStringUtils", GalaxyStringUtils.class);
        System.out.println(utils==null);
    }

}
