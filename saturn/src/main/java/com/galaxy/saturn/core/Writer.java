package com.galaxy.saturn.core;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/24 9:52
 **/
public class Writer implements Runnable{

    private boolean stop = false;

    private boolean alreadyDie = false;

    public boolean isAlreadyDie() {
        return alreadyDie;
    }

    @Override
    public void run() {

    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
