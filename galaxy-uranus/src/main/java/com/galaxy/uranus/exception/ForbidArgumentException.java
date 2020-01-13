package com.galaxy.uranus.exception;

/**
 * 禁止接受参数异常。
 * 该异常用于表示当对命令行输入参数进行解析时，解析完成后，发现上一个
 * 参数项还需要接受参数，此时则会抛出该异常。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 23:00 2019/12/23
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
