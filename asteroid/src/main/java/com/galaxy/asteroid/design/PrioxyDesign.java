package com.galaxy.asteroid.design;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2019/3/14 17:06
 **/
public class PrioxyDesign {

    private static class InvocationHandle<T> implements InvocationHandler {

        private T object;

        public InvocationHandle(T object) {
            this.object = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("开始执行");
            long st = System.currentTimeMillis();
            Object result = method.invoke(object);
            System.out.println("耗时:" + (System.currentTimeMillis() - st));
            return result;
        }
    }

    private static class Student {
        private String name;

        private String schoolName;


    }


}
