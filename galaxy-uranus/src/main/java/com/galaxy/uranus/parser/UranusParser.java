package com.galaxy.uranus.parser;

import com.galaxy.stone.Symbol;
import com.galaxy.uranus.CommandLine;
import com.galaxy.uranus.exception.ForbidArgumentException;
import com.galaxy.uranus.exception.UnAnalysisException;
import com.galaxy.uranus.exception.UnCompleteException;
import com.galaxy.uranus.exception.UranusException;
import com.galaxy.uranus.option.Option;
import com.galaxy.uranus.option.OptionGroup;
import com.galaxy.uranus.option.Options;
import com.galaxy.uranus.utils.OptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 参数解析器
 * 1、解析以下风格的参数:
 * -K
 * -K V
 * -Key
 * -Key V
 * -K1K2K3
 * -DKey=V
 * --K
 * --K V
 * --Key
 * --Key V
 * --DKey=V
 * @Date : Create in 22:10 2019/12/18
 * @Modified By:
 */
@SuppressWarnings("WeakerAccess")
public class UranusParser {

	/**
	 *
	 */
	private Options options;

	/**
	 * 命令行对象
	 */
	private CommandLine commandLine;

	/**
	 * 当前正在处理的参数项
	 */
	private Option currentOption;

	/**
	 * 当前正在处理的参数组
	 */
	private OptionGroup currentOptionGroup;

	/**
	 * 未知参数项
	 */
	private List<String> unknownToken;

	public UranusParser() {
		unknownToken = new ArrayList<>();
	}

	public CommandLine parse(Options options, String[] args) throws UranusException {
		this.options = options;
		commandLine = new CommandLine();
		for (String token : args) {
			handleToken(token);
		}

		if (currentOption != null && currentOptionGroup.isComplete()) {
			commandLine.add(currentOptionGroup);
		}

		// 检查参数缓存器是否已经输入完成
		if (!options.isComplete()) {
			throw new UnCompleteException(options.getOptionGroup(group -> !group.isComplete(), "")
					.getOption(option -> (option.isRequired() && !option.hasIpt()) || (option.acceptArgs())));
		}
		// 设置未处理或为解析的参数项
		commandLine.setUnknownToken(unknownToken);
		// 清除所有命令行未输入的参数项
		commandLine.getOptionGroups().forEach(OptionGroup::clearUnInputOption);
		return commandLine;
	}

	/**
	 * 处理输入的参数Token
	 * 1、参数值
	 * 2、参数项
	 *
	 * @param token 参数token
	 */
	private void handleToken(String token) throws UnAnalysisException, ForbidArgumentException {
		// 1 当前参数项不为空且未接受参数值状态
		if (currentOption != null && currentOption.acceptArgs()) {
			currentOption.addValue(token);
		}
		// 2 处理长参项
		else if (token.startsWith(Symbol.DOUBLE_SHORT_RUNG.getValue())) {
			handleLongToken(token);
		}
		// 3 处理短参项
		else if (token.startsWith(Symbol.SHORT_RUNG.getValue())) {
			handleShortToken(token);
		}
		// 4 处理未知参数项
		else {
			unknownToken.add(token);
		}
	}

	/**
	 * 处理短参项
	 * -K
	 * -k
	 * -K1K2
	 * -K=V
	 * -Dk=V
	 * -Key=V
	 * -Key
	 *
	 * @param token 短参数项
	 */
	private void handleShortToken(String token) throws UnAnalysisException, ForbidArgumentException {
		// 去除 -
		token = token.substring(1);
		int pos = token.indexOf(Symbol.EQUAL_SIGN.getValue());
		// -K=V
		if (pos > 0) {
			handleTokenWithEquals(token, pos, Option::getOpt);
		}
		// -K or -K1K2
		else if (pos == -1) {
			handleShortTokenWithoutEquals(token);
		} else {
			unknownToken.add(token);
		}
	}

	/**
	 * 处理无等于号的短参
	 *
	 * @param token 命令行TOKEN
	 * @throws ForbidArgumentException 参数异常
	 */
	private void handleShortTokenWithoutEquals(String token) throws ForbidArgumentException {
		//-K or key
		Option option = options.getOption(opt -> opt.getOpt().equals(token));
		if (option != null) {
			handleOption(option);
		}
		// -K1K2K3
		else if (token.length() > 1) {
			for (char singleToken : token.toCharArray()) {
				Option obtain = options.getOption(opt -> opt.getOpt().equals(String.valueOf(singleToken)));
				if (obtain == null) {
					unknownToken.add(String.valueOf(singleToken));
					break;
				}
				handleOption(obtain);
			}
		} else {
			unknownToken.add(token);
		}
	}

	/**
	 * 处理长参数项
	 * --K
	 * --k
	 * --K=V
	 * --Dk=V
	 * --key
	 * --key=V
	 *
	 * @param token 参数项
	 */
	private void handleLongToken(String token) throws ForbidArgumentException, UnAnalysisException {
		token = token.substring(2);
		int pos = token.indexOf(Symbol.EQUAL_SIGN.getValue());
		if (pos > 0) {
			handleTokenWithEquals(token, pos, Option::getLongOpt);
		} else if (pos == -1) {
			handleLongTokenWithOutEquals(token);
		} else {
			unknownToken.add(token);
		}
	}

	/**
	 * 处理待等于号的参数
	 * -K=V
	 * -Key=V
	 * -DKey=V
	 * --K=V
	 * --Key=v
	 * --DKey=V
	 *
	 * @param token    命令行输入参数
	 * @param pos      等于号位置
	 * @param function 参数获取函数
	 * @throws ForbidArgumentException 接受参数异常
	 * @throws UnAnalysisException     不能分析异常
	 */
	private void handleTokenWithEquals(String token, int pos, Function<Option, String> function) throws ForbidArgumentException, UnAnalysisException {
		final String key = token.substring(0, pos);
		final String value = token.substring(pos + 1);
		Option option = options.getOption(opt -> function.apply(opt).equals(key));
		if (option != null) {
			option.addValue(value);
			handleOption(option);
		} else if (!OptionUtils.isJavaProperty(key)) {
			throw new UnAnalysisException(String.format("不能解析输入参数:%s", token));
		}
		final String subKey = key.substring(1);
		option = options.getOption(opt -> function.apply(opt).equals(subKey));
		if (option != null) {
			option.addValue(value);
			handleOption(option);
		} else {
			throw new UnAnalysisException(String.format("不能解析输入参数:%s", token));
		}
	}

	/**
	 * 处理不带等于号的长参项
	 * --K
	 * --Key
	 *
	 * @param token 命令行输入参数
	 */
	private void handleLongTokenWithOutEquals(String token) throws ForbidArgumentException, UnAnalysisException {
		Option option = options.getOption(opt -> opt.getLongOpt().equals(token));
		if (option != null) {
			handleOption(option);
		} else {
			throw new UnAnalysisException(String.format("不能解析输入参数:%s", token));
		}
	}

	/**
	 * 处理命令行接受的参数项
	 *
	 * @param option 参数项
	 * @throws ForbidArgumentException 参数值接受异常
	 */
	private void handleOption(Option option) throws ForbidArgumentException {
		if (currentOption != null && currentOption.acceptArgs()) {
			throw new ForbidArgumentException(String.format("参数项:%s未输入完毕", currentOption.getOpt()));
		}
		// 设置输入标志
		option.setHasIpt(true);
		if (option.acceptArgs()) {
			currentOption = option;
		}
		currentOptionGroup = option.getOptionGroup();
		if (currentOptionGroup != null && currentOptionGroup.isComplete()) {
			commandLine.add(currentOptionGroup);
		}
	}
}
