package com.galaxy.saturn.application;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  默认的文件输入方法
 * @date 2018/12/24 9:53
 **/
public class DefaultInput implements Input {

    /**
     * 单例实例化标志，用于防止被反射攻击
     */
    private static boolean INSTANCE = false;

    private DefaultInput() {
        if (INSTANCE) {
            throw new RuntimeException("can not create more instance !");
        }
        INSTANCE = true;
    }

    private static class Inner {
        private static final DefaultInput TOOL = new DefaultInput();
    }

    static DefaultInput getInstance() {
        return Inner.TOOL;
    }

    @Override
    public String take(String line) {
        return line;
    }
}
