package com.galaxy.stone;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:46 2019/8/14
 * @Modified By:
 */
public enum Symbol {
	/**
	 * 空值，短横杠，双短横杠，点号，斜杠，等于号
	 */
	EMPTY_STR(""),
	SHORT_RUNG("-"),
	DOUBLE_SHORT_RUNG("--"),
	DOT("."),
	SLASH("/"),
	EQUAL_SIGN("=");

	String value;

	Symbol(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
