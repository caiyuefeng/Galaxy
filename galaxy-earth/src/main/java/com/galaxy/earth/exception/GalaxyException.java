package com.galaxy.earth.exception;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 19:34 2019/12/30
 *
 */
public class GalaxyException extends Exception {

	public GalaxyException(String message, Throwable e) {
		super(message, e);
	}

	public GalaxyException(String message) {
		super(message);
	}
}
