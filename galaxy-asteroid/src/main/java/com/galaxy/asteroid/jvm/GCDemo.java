package com.galaxy.asteroid.jvm;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 20:36 2019/3/28
 *
 */
public class GCDemo {

    private static class Instance {
        private Instance instance;

        // 占内存
        private byte[] bytes = new byte[1024 * 1024];
    }

    /**
     * 测试JVM GC判定方式
     * 引用计数法
     * 两个对象相互引用，但不被其他对象引用
     * 此时是否回收内存
     * -XX:+PrintGCDetails
     */
    private static void testIncrease() {
        Instance a = new Instance();
        Instance b = new Instance();
        a.instance = b;
        b.instance = a;
        a = null;
        b = null;
        // 发生GC
        System.gc();
    }

    private static class FinalizeEscapeGC {

        public static FinalizeEscapeGC SAVE = null;

        public void isAlive() {
            System.out.println("我还活着");
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            System.out.println("finalize方法被调用");
            // 添加一个引用
            FinalizeEscapeGC.SAVE = this;
        }
    }

    private static void testFinalizeMethod() throws InterruptedException {
        FinalizeEscapeGC.SAVE = FinalizeEscapeGC.SAVE == null ?
                new FinalizeEscapeGC() : FinalizeEscapeGC.SAVE;

        // 引用清除,对象自救
        FinalizeEscapeGC.SAVE = null;
        System.gc();
        // finalize方法执行优先级低，暂停0.5秒执行
        Thread.sleep(500);
        if (FinalizeEscapeGC.SAVE != null) {
            FinalizeEscapeGC.SAVE.isAlive();
        } else {
            System.out.println("我已经死了");
        }

    }


    public static void main(String[] args) throws InterruptedException {
        // 测试GC时互相引用是否被回收
        testIncrease();
        // 第一次执行对象成功自救
//        testFinalizeMethod();
//         第二次执行对象自救失败
//        testFinalizeMethod();
    }

}
