package com.galaxy.uranus.exception;

/**
 * 参数解析器基异常。
 * 所有参数解析的异常均继承于该异常。
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 20:38 2019/12/23
 */
@SuppressWarnings("WeakerAccess")
public class UranusException extends Exception {

	public UranusException(String message, Throwable e) {
		super(message, e);
	}

	public UranusException(String message) {
		super(message);
	}
}
