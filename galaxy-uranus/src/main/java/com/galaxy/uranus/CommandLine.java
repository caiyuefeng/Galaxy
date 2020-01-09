package com.galaxy.uranus;

import com.galaxy.uranus.option.Option;
import com.galaxy.uranus.option.OptionGroup;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 命令行输入的参数项 实例
 * @Date : Create in 22:11 2019/12/18
 * @Modified By:
 */
@SuppressWarnings("WeakerAccess")
public class CommandLine {

	/**
	 * 参数项组缓存
	 * 参数项组组名-参数项组实例
	 */
	private Map<String, OptionGroup> optionGroupMap = new HashMap<>();

	/**
	 * 命令行输入未能解析参数集
	 */
	private List<String> unknownToken = new ArrayList<>();

	public void add(final OptionGroup optionGroup) {
		optionGroupMap.put(optionGroup.getGroupName(), optionGroup);
	}

	/**
	 * 通过参数组的组名获取对应的参数组
	 *
	 * @param groupName 参数组 组名
	 * @return 参数组对象
	 */
	public OptionGroup get(String groupName) {
		return optionGroupMap.get(groupName);
	}

	/**
	 * 通过参数项获取所在参数组
	 * 获取方式通过参数项的短参名和长参名进行获取
	 *
	 * @param option 参数项
	 * @return 参数组对象
	 */
	public OptionGroup get(Option option) {
		return optionGroupMap.values().stream().filter(optionGroup -> optionGroup.hasOption(option)).findFirst().orElse(null);
	}

	/**
	 * 获取匹配指定参数组名内的谓词的参数项
	 *
	 * @param match 谓词
	 * @return 目标参数项集合
	 */
	public List<Option> get(Match<Option> match) {
		return optionGroupMap.entrySet().stream().flatMap(entry ->
				entry.getValue().getAllOption().stream()
						.filter(opt -> match.match(entry.getKey(), opt))).collect(Collectors.toList());
	}

	public List<OptionGroup> getOptionGroups() {
		return Collections.unmodifiableList(new ArrayList<>(optionGroupMap.values()));
	}

	public List<String> getUnknownToken() {
		return Collections.unmodifiableList(unknownToken);
	}

	public void setUnknownToken(final List<String> unknownToken) {
		this.unknownToken = unknownToken;
	}

	/**
	 * 参数组名，及参数组其他要素匹配器
	 *
	 * @param <T> 参数组其他要素
	 */
	@FunctionalInterface
	public interface Match<T> {
		boolean match(String groupName, T t);
	}
}
