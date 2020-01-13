package com.galaxy.uranus.exception;

/**
 * 不能分析异常。
 * 当命令行输入一个参数后，解析器解析时，作为短参或者长参解析时未能
 * 正确的识别该参数的格式则会抛出该异常。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 22:55 2019/12/23
 */
@SuppressWarnings("unused")
public class UnAnalysisException extends UranusException {

	public UnAnalysisException(String message, Throwable e) {
		super(message, e);
	}

	public UnAnalysisException(String token) {
		super(String.format("不能解析输入参数:%s", token));
	}
}
