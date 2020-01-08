package com.galaxy.uranus.parser;

import com.galaxy.earth.GalaxyLog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

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
	 * 默认Map初始化大小
	 */
	private static final int ZERO = 0;

	/**
	 * 预期参数项集合
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
	 * 命令行未能解析的输入参数
	 */
	private List<String> unknownToken;
	/**
	 * 参数处理操作缓存
	 */
	private Predicate<String>[] handleProcess;
	/**
	 * 参数处理执行流程
	 */
	private int[] handleSequence = new int[]{0, 1, 2, 3};

	public UranusParser() {
		unknownToken = new ArrayList<>();
		initHandleProcess();
	}

	/**
	 * 初始化参数处理流程
	 */
	private void initHandleProcess() {
		//noinspection unchecked
		handleProcess = new Predicate[4];
		// 1 当前参数项不为空且未接受参数值状态
		handleProcess[0] = token -> {
			boolean result = currentOption != null && currentOption.acceptArgs();
			if (result) {
				currentOption.addValue(token);
			}
			return result;
		};
		// 2 处理长参项
		handleProcess[1] = token -> {
			boolean result = token.startsWith(Symbol.DOUBLE_SHORT_RUNG.getValue());
			if (result) {
				try {
					handleLongToken(token);
				} catch (ForbidArgumentException | UnAnalysisException e) {
					GalaxyLog.FILE_ERROR(String.format("参数%s处理异常", token), e);
					throw new IllegalStateException(e);
				}
			}
			return result;
		};
		// 3 处理短参项
		handleProcess[2] = token -> {
			boolean result = token.startsWith(Symbol.SHORT_RUNG.getValue());
			if (result) {
				try {
					handleShortToken(token);
				} catch (UnAnalysisException | ForbidArgumentException e) {
					GalaxyLog.FILE_ERROR(String.format("参数%s处理异常", token), e);
					throw new IllegalStateException(e);
				}
			}
			return result;
		};
		// 4 处理未知参数项
		handleProcess[3] = token -> {
			unknownToken.add(token);
			return true;
		};
	}

	/**
	 * 解析命令行参数
	 * 该解析方式下:
	 * 1、参数项无默认参数值
	 * 2、接受以-或--开头的参数值
	 *
	 * @param options 预期参数项集合
	 * @param args    命令行参数
	 * @return 命令行参数项
	 * @throws UranusException 解析异常
	 */
	public CommandLine parse(Options options, String[] args) throws UranusException {
		return parse(options, args, new HashMap<>(ZERO), true);
	}

	/**
	 * 解析带有默认值的参数项
	 * 该解析方式默认接受以-或--开头的参数值
	 *
	 * @param options    预期参数项集合
	 * @param args       命令行参数
	 * @param properties 默认参数值缓存
	 * @return 命令行参数项集合
	 * @throws UranusException 解析异常
	 */
	public CommandLine parse(Options options, String[] args, Map<String, List<String>> properties) throws UranusException {
		return parse(options, args, properties, true);
	}

	/**
	 * 按照预期的所有参数项解析命令行输入的参数
	 *
	 * @param options                    预期参数项集合
	 * @param args                       命令行输入参数
	 * @param properties                 参数项默认值
	 * @param isAcceptOptionSignArgument 是否接受-或--开头的参数值，即是否把以-或--开头的
	 *                                   都解释未参数项而非参数值，当参数项的参数值未可选参数值时
	 *                                   命令行连续输入多个参数项，则此时需要将该参数设置未false
	 *                                   以让所有参数项都正确解析
	 * @return 命令行参数项实例
	 * @throws UranusException 解析异常
	 */
	public CommandLine parse(Options options, String[] args, Map<String, List<String>> properties, boolean isAcceptOptionSignArgument) throws UranusException {
		this.options = options;
		// 根据标志为调整执行顺序
		if (!isAcceptOptionSignArgument) {
			handleSequence[0] = 1;
			handleSequence[1] = 2;
			handleSequence[2] = 0;
		}
		commandLine = new CommandLine();
		for (String token : args) {
			handleToken(token);
		}
		// 将最后的参数组添加至命令行参数项
		if (currentOption != null && currentOptionGroup != null) {
			commandLine.add(currentOptionGroup);
		}

		// 设置未处理或为解析的参数项
		commandLine.setUnknownToken(unknownToken);
		// 为参数项添加默认参数值
		commandLine.getOptionGroups().forEach(optionGroup -> properties.forEach((opt, values) -> {
			Option option = optionGroup.getOption(obtain -> obtain.getOpt().equals(opt));
			if (option.isOptionalArg() && option.acceptArgs()) {
				values.forEach(option::addValue);
			}
		}));
		// 检查参数缓存器是否已经输入完成
		if (!options.isComplete()) {
			throw new UnCompleteException(options.getOptionGroup(group -> !group.isComplete(), "")
					.getOption(option -> (option.isRequired() && !option.hasIpt()) || (option.acceptArgs())));
		}
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
	private void handleToken(String token) {
		for (int sequence : handleSequence) {
			if (handleProcess[sequence].test(token)) {
				break;
			}
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
			throw new UnAnalysisException(token);
		}
		final String subKey = key.substring(1);
		option = options.getOption(opt -> function.apply(opt).equals(subKey));
		if (option != null) {
			option.addValue(value);
			handleOption(option);
		} else {
			throw new UnAnalysisException(token);
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
			throw new UnAnalysisException(token);
		}
	}

	/**
	 * 处理命令行接受的参数项
	 *
	 * @param option 参数项
	 * @throws ForbidArgumentException 参数值接受异常
	 */
	private void handleOption(Option option) throws ForbidArgumentException {
		if (currentOption != null && !currentOption.isComplete()) {
			throw new ForbidArgumentException(String.format("参数项:%s未输入完毕", currentOption.getOpt()));
		}
		// 设置输入标志
		option.setHasIpt(true);
		if (option.acceptArgs()) {
			currentOption = option;
		}
		currentOptionGroup = option.getOptionGroup();
		if (currentOptionGroup != null) {
			commandLine.add(currentOptionGroup);
		}
	}
}
