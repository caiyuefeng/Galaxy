package com.galaxy.earth;

import com.galaxy.stone.ConfigurationHelp;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 14:57 2019/12/29
 *
 */
public class GalaxyLog {

	/**
	 * 日志文件句柄
	 */
	private static final Logger LOG_FILE = LoggerFactory.getLogger("DailyFile");

	private static final Logger LOG_CONSOLE = LoggerFactory.getLogger("std");

	static {
		// 设置log4j输出路径
		String logDir = System.getProperty("log.dir");
		logDir = StringUtils.isEmpty(logDir) ? ConfigurationHelp.getGalaxyHome() + "/log" : logDir;
		System.setProperty("log.dir", logDir);
		PropertyConfigurator.configure(ConfigurationHelp.getGalaxyHome() + "/etc/log4j.properties");
	}

	public static void CONSOLE_INFO(String message, Object... values) {
		LOG_CONSOLE.info(message, values);
	}

	public static void CONSOLE_INFO(String message) {
		LOG_CONSOLE.info(message);
	}

	public static void CONSOLE_ERROR(String message, Object... value) {
		LOG_CONSOLE.error(message, value);
	}

	public static void CONSOLE_ERROR(String message, Throwable e) {
		LOG_CONSOLE.error(message, e);
	}

	public static void CONSOLE_FILE_ERROR(String message, Throwable e) {
		LOG_CONSOLE.error(message, e);
		LOG_FILE.error(message, e);
	}

	public static void FILE_INFO(String message) {
		LOG_FILE.info(message);
	}

	public static void FILE_INFO(String message, Object... values) {
		LOG_FILE.info(message, values);
	}

	public static void FILE_ERROR(String message) {
		LOG_FILE.error(message);
	}

	public static void FILE_ERROR(String message, Object... values) {
		LOG_FILE.error(message, values);
	}
}
