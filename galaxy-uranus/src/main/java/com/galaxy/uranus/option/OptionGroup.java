package com.galaxy.uranus.option;

import com.galaxy.uranus.exception.MultiOptionGroupException;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 参数组
 * 用于存储一组表示相同含义的不同参数
 * 如 同一组运行同一功能的不同实现
 * @Date : Create in 22:09 2019/12/18
 * @Modified By:
 */
public class OptionGroup {

	/**
	 * 短参名映射
	 */
	private Map<String, Option> opts;

	/**
	 * 长参名映射
	 */
	private Map<String, Option> longOpts;

	/**
	 * 参数组名称
	 */
	private String groupName;

	/**
	 * 该参数组命令行是否必须输入
	 * 当参数组内的参数项都是选填的
	 * 而参数组又是必须从命令行输入的，
	 * 即命令行输入该参数组内参数项至少一个
	 */
	private boolean isRequired;

	public OptionGroup(String groupName) {
		this(groupName, false);
	}

	public OptionGroup(String groupName, boolean isRequired) {
		this.groupName = groupName;
		this.isRequired = isRequired;
		this.opts = new HashMap<>();
		this.longOpts = new HashMap<>();
	}

	/**
	 * 向参数组中添加一个新的参数项
	 *
	 * @param option 参数项
	 * @throws MultiOptionGroupException 多参数组异常
	 */
	public void addOption(Option option) throws MultiOptionGroupException {
		// 检查新增参数项是否已经指定过参数组
		if (option.getOptionGroup() != null && !this.equals(option.getOptionGroup())) {
			throw new MultiOptionGroupException(option);
		}
		opts.put(option.getOpt(), option);
		if (StringUtils.isNotEmpty(option.getLongOpt())) {
			longOpts.put(option.getLongOpt(), option);
		}
		// 设置参数组
		option.setOptionGroup(this);
	}

	public Option getOption(Predicate<Option> predicate) {
		return opts.values().stream().filter(predicate).findFirst().orElse(null);
	}

	/**
	 * 判断当前参数组是否已经接受完毕
	 *
	 * @return 判断结果
	 */
	public boolean isComplete() {
		// 所有已经接受的参数，参数值已经接受完毕
		boolean match2 = opts.values().stream().noneMatch(Option::isRequired) &&
				opts.values().stream().allMatch(option -> !option.hasIpt() || (option.hasIpt() && !option.acceptArgs()));
		// 1 如果参数组时必填的
		if (isRequired()) {
			// 1.1 必须输入的参数已经输入且输入参数满足
			boolean match1 = opts.values().stream().anyMatch(Option::isRequired) && opts.values().stream().filter(Option::isRequired).allMatch(option -> option.hasIpt() && !option.acceptArgs());
			// 1.2 所有参数项非必须，所有已经输入的命令行参数值已经接受完毕,且存在至少一个已经从命令行输入
			boolean match3 = opts.values().stream().filter(opt -> !opt.isRequired()).anyMatch(Option::hasIpt);
			return match1 || (match2 && match3);
		}
		// 如果参数组非必填，则要求所有已经输入的参数项 参数值已经接受完毕即可
		return match2;
	}

	private boolean isRequired() {
		return isRequired || opts.values().stream().anyMatch(Option::isRequired);
	}

	public String getGroupName() {
		return groupName;
	}

	public int getGroupSize() {
		return opts.values().size();
	}

	public void setRequired(boolean required) {
		isRequired = required;
	}

	public void clearUnInputOption() {
		List<Option> shortInput = opts.values().stream()
				.filter(Option::hasIpt).collect(Collectors.toList());
		opts.clear();
		longOpts.clear();
		// 保存命令行输入的参数项
		shortInput.forEach(opt -> {
			opts.put(opt.getOpt(), opt);
			longOpts.put(opt.getLongOpt(), opt);
		});
	}
}
