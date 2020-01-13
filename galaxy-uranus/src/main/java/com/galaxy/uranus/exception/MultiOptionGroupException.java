package com.galaxy.uranus.exception;

import com.galaxy.uranus.option.Option;

/**
 * 参数项指定多个参数组异常。
 * 当一个参数项指向多个参数组时，则会抛出该异常。例如，当初始化一个参数项后，
 * 间隔多个操作后再次指定一个新参数组给该参数项则会抛出该一次。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 20:39 2019/12/23
 */
@SuppressWarnings("unused")
public final class MultiOptionGroupException extends UranusException {

	public MultiOptionGroupException(String message, Throwable e) {
		super(message, e);
	}

	public MultiOptionGroupException(Option option) {
		super("参数项" + option.getOpt() + "存在多个参数组");
	}
}
