package com.galaxy.stone;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 22:45 2019/8/14
 *
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
