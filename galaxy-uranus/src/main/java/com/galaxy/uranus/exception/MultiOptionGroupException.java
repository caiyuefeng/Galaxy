package com.galaxy.uranus.exception;

import com.galaxy.uranus.option.Option;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 20:39 2019/12/23
 * @Modified By:
 */
@SuppressWarnings("unused")
public final class MultiOptionGroupException extends UranusException {

	public MultiOptionGroupException(String message,Throwable e){
		super(message,e);
	}

	public MultiOptionGroupException(Option option) {
		super("参数项" + option.getOpt() + "存在多个参数组");
	}
}
