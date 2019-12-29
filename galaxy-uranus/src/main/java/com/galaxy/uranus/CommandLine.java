package com.galaxy.uranus;

import com.galaxy.uranus.option.Option;
import com.galaxy.uranus.option.OptionGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:11 2019/12/18
 * @Modified By:
 */
public class CommandLine {

	/**
	 * 参数项组缓存
	 * 参数项组组名-参数项组实例
	 */
	private Map<String, OptionGroup> optionGroupMap = new HashMap<>();

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
		return new OptionGroup("");
	}

	public List<OptionGroup> getOptionGroups() {
		return new ArrayList<>(optionGroupMap.values());
	}

	public List<String> getUnknownToken() {
		return unknownToken;
	}

	public void setUnknownToken(List<String> unknownToken) {
		this.unknownToken = unknownToken;
	}
}
