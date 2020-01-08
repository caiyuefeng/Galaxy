package com.galaxy.uranus.exception;

import com.galaxy.uranus.option.Option;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:00 2019/12/23
 * @Modified By:
 */
@SuppressWarnings("unused")
public class UnCompleteException extends UranusException {

	public UnCompleteException(String message, Throwable e) {
		super(message, e);
	}

	@SuppressWarnings("WeakerAccess")
	public UnCompleteException(String message) {
		super(message);
	}

	public UnCompleteException(Option option) {
		this(option == null ? "命令行未输入必须的参数" : !option.hasIpt() ? String.format("命令行未输入必须的参数项 %s", option.getOpt()) :
				String.format("参数项输入参数不足%d个参数", option.getNumOfArgs()));
	}
}
