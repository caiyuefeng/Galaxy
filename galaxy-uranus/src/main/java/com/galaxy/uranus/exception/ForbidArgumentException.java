package com.galaxy.uranus.exception;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 23:00 2019/12/23
 *
 */
@SuppressWarnings("unused")
public class ForbidArgumentException extends UranusException {

	public ForbidArgumentException(String message, Throwable e) {
		super(message, e);
	}

	public ForbidArgumentException(String message) {
		super(message);
	}
}
