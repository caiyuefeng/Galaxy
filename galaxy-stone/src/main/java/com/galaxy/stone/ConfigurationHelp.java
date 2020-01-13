package com.galaxy.stone;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 13:37 2019/12/29
 *
 */
public class ConfigurationHelp {

	/**
	 * 配置属性
	 */
	private static Map<String, String> property = new HashMap<>();

	static {
		// TODO 修改未读取XML文件
//		property = loadProperties(getGalaxyHome() + "/etc/galaxy.properties");
	}

	public static String getGalaxyHome() {
		String galaxyHome = System.getProperty("GALAXY_HOME");
		return isEmpty(galaxyHome) ? "/home/galaxy/galaxy_sdk_1.0" : galaxyHome;
	}

	private static boolean isEmpty(String value) {
		return value == null || "".equals(value);
	}

	private static Map<String, String> loadProperties(String properties) throws IOException {
		try (InputStream in = new FileInputStream(new File(properties))) {
			Properties prop = new Properties();
			prop.load(in);
			Map<String, String> map = new HashMap<>();
			for (Map.Entry<Object, Object> entry : prop.entrySet()) {
				map.put(entry.getKey().toString(), entry.getValue().toString());
			}
			return map;
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}
	}
}
