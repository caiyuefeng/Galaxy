package com.galaxy.uranus.exception;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 22:55 2019/12/23
 *
 */
@SuppressWarnings("unused")
public class UnAnalysisException extends UranusException {

	public UnAnalysisException(String message,Throwable e){
		super(message, e);
	}

	public UnAnalysisException(String token) {
		super(String.format("不能解析输入参数:%s", token));
	}
}
