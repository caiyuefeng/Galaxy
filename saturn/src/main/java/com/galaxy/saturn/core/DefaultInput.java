package com.galaxy.saturn.core;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/24 9:53
 **/
public class DefaultInput implements Input {

    private static class Inner {
        private static final DefaultInput TOOL = new DefaultInput();
    }

    public static DefaultInput getInstance() {
        return Inner.TOOL;
    }

    @Override
    public String take(String line) {
        return line;
    }
}
