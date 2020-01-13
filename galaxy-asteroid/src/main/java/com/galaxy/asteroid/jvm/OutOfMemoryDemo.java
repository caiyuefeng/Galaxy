package com.galaxy.asteroid.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 23:01 2019/3/27
 *
 */
public class OutOfMemoryDemo {

    private static class OOMClass {
    }

    /**
     * VM Args -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError(将当前内存堆存储快照)
     * 设置 Java堆大小为20M,将-Xms和-Xmx设置成一样避免扩展
     * 测试Java堆内存溢出
     */
    private static void testHeapOutOfMemory() {
        List<OOMClass> list = new ArrayList<>();
        while (true) {
            list.add(new OOMClass());
        }
    }

    private static class StackClass {
        private int stackLength = 0;

        public void testStackOverFlow() {
            stackLength++;
            testStackOverFlow();
        }
    }

    /**
     * 测试栈深度溢出
     * -Xss128K
     */
    private static void testStackOver() {
        StackClass stackClass = new StackClass();
        try {
            stackClass.testStackOverFlow();
        }catch (Exception e){
            System.out.println(stackClass.stackLength);
            e.printStackTrace();
        }
    }

    /**
     * 测试运行时常量池内存溢出
     * VM Args -XX:PermSize=10M -XX:MaxPermSize=10M
     *
     */
    private static void testRuntimeConstant(){
        List<String> list = new ArrayList<>();
        int i = 0;
        while (true){
            list.add(String.valueOf(i++).intern());
        }
    }

    private static void testIntern(){
        String str1 = new StringBuilder("计算机").append("软件").toString();
        System.out.println(str1.intern()==str1);
        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern()==str2);



    }


    public static void main(String[] args) {
//        testHeapOutOfMemory();
//        testStackOver();
//        testRuntimeConstant();
        testIntern();
    }

}
