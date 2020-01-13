package com.galaxy.earth.exception;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 19:34 2019/12/30
 *
 */
public class GalaxyIOException extends Exception {

	public GalaxyIOException(String message, Throwable e) {
		super(message, e);
	}

	public GalaxyIOException(String message) {
		super(message);
	}
}
