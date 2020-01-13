package com.galaxy.stone;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 22:46 2019/8/14
 *
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
