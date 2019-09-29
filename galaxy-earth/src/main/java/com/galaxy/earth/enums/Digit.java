package com.galaxy.earth.enums;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:45 2019/8/14
 * @Modified By:
 */
public enum Digit {
    /**
     * 数字
     */
    ZERO(0),ONE(1), TWO(2), FOUR(4);
    int value;

    Digit(int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }
}
