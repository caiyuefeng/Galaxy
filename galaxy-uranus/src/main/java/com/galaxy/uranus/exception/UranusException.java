package com.galaxy.uranus.exception;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 20:38 2019/12/23
 * @Modified By:
 */
public class UranusException extends Exception {

	public  UranusException(String message,Throwable e){
		super(message,e);
	}

	public UranusException(String message) {
		super(message);
	}
}
