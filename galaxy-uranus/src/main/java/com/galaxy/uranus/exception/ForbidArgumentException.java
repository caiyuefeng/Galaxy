package com.galaxy.uranus.exception;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 23:00 2019/12/23
 * @Modified By:
 */
public class ForbidArgumentException extends UranusException {

	public ForbidArgumentException(String message, Throwable e) {
		super(message, e);
	}

	public ForbidArgumentException(String message) {
		super(message);
	}
}
