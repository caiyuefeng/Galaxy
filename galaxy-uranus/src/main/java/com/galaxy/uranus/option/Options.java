package com.galaxy.uranus.option;

import com.galaxy.uranus.exception.MultiOptionGroupException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 参数集
 * 该类用于保存用户自己构建的参数
 * @Date : Create in 22:18 2019/12/18
 * @Modified By:
 */
public class Options {

	/**
	 * 默认参数项组名称
	 */
	private static final String DEFAULT_GROUP_NAME = "default";

	/**
	 * 参数项组缓存
	 * 缓存形式 : Group_Name -> OptionGroup_Instance
	 */
	private Map<String, OptionGroup> optionGroups;

	public Options() {
		optionGroups = new HashMap<>();
	}

	public OptionGroup getOptionGroup(Predicate<OptionGroup> groupPredicate) {
		return optionGroups.values().stream().filter(groupPredicate)
				.findFirst().orElse(new OptionGroup(""));
	}

	/**
	 * 获取符合条件的参数项
	 *
	 * @param predicate 条件
	 * @return 参数项
	 */
	public Option getOption(Predicate<Option> predicate) {
		return optionGroups.values().stream()
				.map(optionGroup -> {
					Option option = optionGroup.getOption(predicate);
					return option == null ? Option.builder("").build() : option;
				}).filter(option -> !"".equals(option.getOpt())).findFirst().orElse(null);
	}

	/**
	 * 当前参数缓存器是否已经接受完毕
	 *
	 * @return true or false
	 */
	public boolean isComplete() {
		return optionGroups.values().stream().allMatch(OptionGroup::isComplete);
	}

	/**
	 * 加入参数项组
	 *
	 * @param optionGroup 参数项组实例
	 */
	public void addOptionGroup(OptionGroup optionGroup) {
		optionGroups.put(optionGroup.getGroupName(), optionGroup);
	}

	/**
	 * 加入参数项
	 * 该方法加入的参数项会默认添加道DEFAULT参数项组中
	 * DEFAULT参数项组默认是非必须的
	 *
	 * @param option 参数项实例
	 * @throws MultiOptionGroupException 多个参数项组异常
	 */
	public void addOption(Option option) throws MultiOptionGroupException {
		OptionGroup optionGroup = optionGroups.getOrDefault(DEFAULT_GROUP_NAME, new OptionGroup(DEFAULT_GROUP_NAME));
		optionGroup.addOption(option);
		option.setOptionGroup(optionGroup);
		optionGroups.put(DEFAULT_GROUP_NAME, optionGroup);
	}
}
