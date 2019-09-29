package com.galaxy.asteroid.jvm;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 内存分配
 * 测试堆内存分配情况
 * @Date : Create in 20:41 2019/4/3
 * @Modified By:
 */
public class GCDemoTwo {

    /**
     * -XX:UserSerialGC 使用SerialGC收集器
     * -Xms20M -Xmx20M 分配20M大小的堆内存
     * -Xmn10M 新生代分配10M内存
     * -XX:+PrintGCDetails 打印GC日志
     */
    private static void testAllocate() {
        byte[] a1 = new byte[2 * 1024 * 1024];
        byte[] a2 = new byte[2 * 1024 * 1024];
        byte[] a3 = new byte[2 * 1024 * 1024];
        // 分配该数组时，由于Eden区域内存不足触发MinorGC
        // 将先前加入的三个数组移入老年代后，将该数组置于Eden区域
        byte[] a4 = new byte[4 * 1024 * 1024];

    }

    public static void main(String[] args) {
//            testAllocate();
        while (true){

        }
    }
}
