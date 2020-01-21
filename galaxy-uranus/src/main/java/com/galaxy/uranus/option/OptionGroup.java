package com.galaxy.uranus.option;

import com.galaxy.uranus.exception.MultiOptionGroupException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 参数组。
 * 用于存储一组表示相同含义的不同参数集合，如同一组运行同一功能的不同实现。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 22:09 2019/12/18
 */
public class OptionGroup {

	/**
	 * 参数项缓存。
	 * 缓存格式：短参名-> {@link Option}。
	 */
	private Map<String, Option> opts;

	/**
	 * 参数组名称。
	 * 该字段用于唯一标识一个参数组。
	 */
	private String groupName;

	/**
	 * 参数组必须标识。
	 * 该标识用于表示参数组在预期参数集中是否是必须输入的，
	 * 如果标识未必须输入，则在命令行输入时，至少需要输入一个
	 * 该参数组的参数项。
	 */
	private boolean isRequired;

	/**
	 * 构建指定参数组名的参数组对象实例。
	 *
	 * @param groupName 参数组名
	 */
	public OptionGroup(String groupName) {
		this(groupName, false);
	}

	/**
	 * 构建指定参数组名，且指定是否必须输入的参数组对象实例。
	 *
	 * @param groupName  参数名
	 * @param isRequired 必须输入标识
	 */
	public OptionGroup(String groupName, boolean isRequired) {
		this.groupName = groupName;
		this.isRequired = isRequired;
		this.opts = new HashMap<>();
	}

	/**
	 * 向参数组中添加一个新的参数项。
	 *
	 * @param option 参数项
	 * @throws MultiOptionGroupException 多参数组异常
	 */
	public void addOption(Option option) throws MultiOptionGroupException {
		// 检查新增参数项是否已经指定过参数组
		if (option.getOptionGroup() != null && !this.groupName.equals(option.getOptionGroup().groupName)) {
			throw new MultiOptionGroupException(option);
		}
		opts.put(option.getOpt(), option);
		// 设置参数组
		option.setOptionGroup(this);
	}

	/**
	 * 获取符合Option lambda表达式的第一个参数项。
	 * 如果参数组中无符合条件的参数项，则返回NULL。
	 * <p>
	 * 该类提供了一个带有默认值的重载方法 {@link OptionGroup#getFirstOption(Predicate, Option)}
	 *
	 * @param predicate Option lambda 表达式
	 * @return 参数项
	 */
	public Option getFirstOption(Predicate<Option> predicate) {
		return getFirstOption(predicate, null);
	}

	/**
	 * 获取符合 Option lambda表达式的第一个参数项，若未获取到，
	 * 则返回默认的参数项。
	 * <p>
	 * 该类同时提供了一个不带有默认值的重载方法{@link OptionGroup#getFirstOption(Predicate)}
	 *
	 * @param predicate     匹配参数项谓词
	 * @param defaultOption 默认参数项
	 * @return 匹配的参数项
	 */
	@SuppressWarnings("WeakerAccess")
	public Option getFirstOption(Predicate<Option> predicate, Option defaultOption) {
		return opts.values().stream().filter(predicate).findFirst().orElse(defaultOption);
	}

	/**
	 * @return 参数组内所有参数项
	 */
	public List<Option> getAllOption() {
		return Collections.unmodifiableList(new ArrayList<>(opts.values()));
	}

	/**
	 * 检查当前参数组是否已经输入完成。
	 * 参数组输入完成有以下情况：
	 * <ol>
	 *     <li>参数组本身时必填的，且组内无必填参数项，此时命令行只要输入一个
	 *     属于该参数组的参数项即可；</li>
	 *      <li>组内存在必填参数项，此时命令行至少需要输入完成所有必填参数项；</li>
	 *      <li>参数组非必填，且组内不存在必填参数项，此时命令不输入该参数组的
	 *      参数项，或者已输入的参数项参数值均已输入完毕。</li>
	 * </ol>
	 * 如何判定参数组是否必填，参考{@link OptionGroup#isRequired()} 方法。
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

	/**
	 * 检查参数组是否必填。
	 * 必填分以下两种情况：
	 * <ul>
	 *     <li>参数组本身时必填的</li>
	 *     <li>参数组内存在必填的参数项</li>
	 * </ul>
	 *
	 * @return 检查结果
	 */
	private boolean isRequired() {
		return isRequired || opts.values().stream().anyMatch(Option::isRequired);
	}

	/**
	 * @return 参数组名
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @return 参数组内包含参数项个数
	 */
	public int getGroupSize() {
		return opts.values().size();
	}

	/**
	 * 清除参数组内所有未输入的参数项。
	 */
	public void clearUnInputOption() {
		List<Option> shortInput = opts.values().stream()
				.filter(Option::hasIpt).collect(Collectors.toList());
		opts.clear();
		// 保存命令行输入的参数项
		shortInput.forEach(opt -> opts.put(opt.getOpt(), opt));
	}

	/**
	 * 检查当前参数组是否包含指定参数项。
	 *
	 * @param option 输入待匹配参数项
	 * @return 检查结果
	 */
	public boolean hasOption(Option option) {
		return opts.values().stream().anyMatch(opt -> opt.equals(option));
	}
}
