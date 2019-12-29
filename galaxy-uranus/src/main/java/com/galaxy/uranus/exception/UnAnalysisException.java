package com.galaxy.uranus.exception;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:55 2019/12/23
 * @Modified By:
 */
@SuppressWarnings("unused")
public class UnAnalysisException extends UranusException {

	public UnAnalysisException(String message,Throwable e){
		super(message, e);
	}

	public UnAnalysisException(String message) {
		super(message);
	}
}
