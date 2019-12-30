package com.galaxy.earth.exception;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 19:34 2019/12/30
 * @Modified By:
 */
public class GalaxyIOException extends Exception {

	public GalaxyIOException(String message, Throwable e) {
		super(message, e);
	}

	public GalaxyIOException(String message) {
		super(message);
	}
}
