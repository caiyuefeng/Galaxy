package com.galaxy.uranus;

import com.galaxy.uranus.option.Option;
import com.galaxy.uranus.option.Options;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 帮助文档构建器
 * 该工具类通过接受定义好的Options参数项，
 * 显示格式:
 * ${HelpInfo_Head} -> 帮助文档头部信息
 * ${HelpInfo_Command} -> ${execute_command} -{option_name} -{option_value} 执行模式或方式
 * ${HelpInfo_Examples} -> 执行样例
 * ${HelpInfo_Option} -> 参数项说明
 * ${HelpInfo_Tail} -> 帮助文档尾部信息
 * 如 GalaxyFramework 运行平台命令
 * galaxy -[start|stop|restart|status] -[Module_Name]
 * com.galaxy.uranus.examples:
 * galaxy -start sirius
 * galaxy -stop sirius
 * options:
 * -start --start
 * @Date : Create in 19:34 2020/1/1
 * @Modified By:
 */
public class HelpInfoFormat {

	private Options options;

	/**
	 * 换行符
	 */
	private static final String LINE_BREAK = "\n";

	/**
	 * 空白符
	 */
	private static final String BLANK = " ";

	public HelpInfoFormat(Options options) {
		this("", "", options);
	}

	public HelpInfoFormat(String headInfo, String exampleInfo, Options options) {
		PrintWriter writer = new PrintWriter(System.out);
		writer.write(headInfo);
		writer.write(LINE_BREAK);
		writer.write("---------------------");
		writer.write(LINE_BREAK);
		writer.write("Examples:");
		writer.write(LINE_BREAK);
		writer.write(exampleInfo);
		writer.write(LINE_BREAK);
		writer.write("---------------------");
		writer.write(LINE_BREAK);
		writer.write("Options:");
		writer.write(LINE_BREAK);
		print(writer, options.getAllOption());
		writer.write("---------------------");
		writer.write(LINE_BREAK);
		writer.flush();
		writer.close();
	}

	private void print(PrintWriter writer, List<Option> options) {
		List<String> prefix = new ArrayList<>();
		List<String> tail = new ArrayList<>();
		int maxLen = 0;
		for (Option option : options) {
			String prefixStr = String.format(" -%s  --%s", option.getOpt(), option.getLongOpt());
			prefix.add(prefixStr);
			tail.add(option.getDesc());
			int currLen = prefixStr.length() + 6;
			maxLen = Math.max(maxLen, currLen);
		}
		for (int i = 0; i < prefix.size(); i++) {
			int currLen = prefix.get(i).length() + 3;
			writer.write(prefix.get(i) + getBlank(maxLen - currLen) + tail.get(i));
			writer.write(LINE_BREAK);
		}
	}

	private final StringBuilder builder = new StringBuilder();

	private String getBlank(int num) {
		builder.setLength(0);
		for (int i = 0; i < num; i++) {
			builder.append(BLANK);
		}
		return builder.toString();
	}
}