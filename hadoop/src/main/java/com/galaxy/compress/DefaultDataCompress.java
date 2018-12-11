package com.galaxy.compress;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 默认数据压缩方法
 * 不压缩数据
 * @date : 2018/12/11 10:19
 **/
public class DefaultDataCompress implements DataCompress {

    private DefaultDataCompress() {
    }

    private static class Inner {
        private final static DefaultDataCompress TOOL = new DefaultDataCompress();
    }

    public static DefaultDataCompress getInstance() {
        return Inner.TOOL;
    }

    @Override
    public String compress(String data) {
        return data;
    }

    @Override
    public String decompress(String data) {
        return data;
    }
}
