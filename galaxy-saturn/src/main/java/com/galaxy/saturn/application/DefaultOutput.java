package com.galaxy.saturn.application;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  默认的文件输出方法
 * @date 2018/12/24 9:53
 **/
public class DefaultOutput implements Output {

    /**
     * 单例实例化标志，用于防止被反射攻击
     */
    private static boolean INSTANCE = false;

    private DefaultOutput() {
        if (INSTANCE) {
            throw new RuntimeException("can not create more instance !");
        }
        INSTANCE = true;
    }

    private static class Inner {
        private static DefaultOutput TOOL = new DefaultOutput();
    }

    public static DefaultOutput getInstance() {
        return Inner.TOOL;
    }

    @Override
    public void take(String line) {

    }

    @Override
    public void end() {

    }
}
