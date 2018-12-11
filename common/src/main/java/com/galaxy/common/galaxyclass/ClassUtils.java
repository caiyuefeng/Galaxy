package com.galaxy.common.galaxyclass;

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
                return (T) Class.forName(className).newInstance();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
