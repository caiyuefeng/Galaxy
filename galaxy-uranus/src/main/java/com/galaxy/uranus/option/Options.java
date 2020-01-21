package com.galaxy.uranus.option;

import com.galaxy.uranus.annotation.*;
import com.galaxy.uranus.exception.MultiOptionGroupException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 预期参数集。
 * 预期参数集接受参数项会以以参数组形式进行接受，当传入单个参数项，
 * 则默认添加到default参数项组中。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 22:18 2019/12/18
 */
@SuppressWarnings("WeakerAccess")
public class Options {

	/**
	 * 默认参数项组名称。
	 */
	private static final String DEFAULT_GROUP_NAME = "default";

	/**
	 * 参数项组缓存。
	 * 缓存形式 : 参数组名 -> 参数组对象实例。
	 */
	private Map<String, OptionGroup> optionGroups;

	/**
	 * 内置的帮助文档参数项。
	 */
	public static final Option HELP_OPTION;

	/**
	 * 内置的版本信息参数项。
	 */
	public static final Option VERSION_OPTION;

	static {
		HELP_OPTION = Option.builder("h").addLongOpt("help").build();
		VERSION_OPTION = Option.builder("v").addLongOpt("version").build();
	}

	public Options() {
		optionGroups = new HashMap<>();
	}

	/**
	 * 获取符合OptionGroup lambda表达式的第一个参数组。
	 * 未获取到时返回以默认组名构建的参数组。
	 *
	 * @param groupPredicate   OptionGroup lambda表达式
	 * @param defaultGroupName 默认参数组名
	 * @return 参数组
	 */
	public OptionGroup getOptionGroup(Predicate<OptionGroup> groupPredicate, String defaultGroupName) {
		return optionGroups.values().stream().filter(groupPredicate)
				.findFirst().orElse(new OptionGroup(defaultGroupName));
	}

	/**
	 * 获取符合Option lambda表达式的第一个参数项。
	 * 未获取到参数项时，返回NULL。
	 *
	 * @param predicate Option lambda表达式
	 * @return 参数项
	 */
	public Option getOption(Predicate<Option> predicate) {
		return optionGroups.values().stream()
				.map(optionGroup -> {
					Option option = optionGroup.getFirstOption(predicate);
					return option == null ? Option.builder("").build() : option;
				}).filter(option -> !"".equals(option.getOpt())).findFirst().orElse(null);
	}

	/**
	 * @return 获取参数集中的所有参数项
	 */
	public List<Option> getAllOption() {
		return optionGroups.values().stream().map(OptionGroup::getAllOption).flatMap(List::stream).collect(Collectors.toList());
	}

	/**
	 * 检查预期参数集是否已经接受完成。
	 * 完成条件时预期参数集中所有参数组均已完成输入。参数组完成条件参考{@link OptionGroup#isComplete()}
	 *
	 * @return true or false
	 * @see OptionGroup
	 */
	public boolean isComplete() {
		return optionGroups.values().stream().allMatch(OptionGroup::isComplete);
	}

	/**
	 * 加入新增参数项组。
	 *
	 * @param optionGroup 参数项组实例
	 */
	public void addOptionGroup(OptionGroup optionGroup) {
		optionGroups.put(optionGroup.getGroupName(), optionGroup);
	}

	/**
	 * 加入新增参数项。
	 * 该方法加入的参数项会默认添加至default参数项组中，且
	 * default参数项组默认是非必须的。
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

	/**
	 * 获取Builder构建器实例。
	 *
	 * @return 构建器实例
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * 构建器
	 * 该构建器默认会加载依赖包路径下面所有带有
	 * OptionAnnotation注解的类，并且将加载的注解解析成参数项
	 */
	@SuppressWarnings("unused")
	public static class Builder {

		private List<OptionGroup> optionGroups;

		private List<Option> options;

		private Builder() {
			optionGroups = new LinkedList<>();
			options = new LinkedList<>();
		}

		public void addOption(Option option) {
			options.add(option);
		}

		public void addOptionGroup(OptionGroup optionGroup) {
			optionGroups.add(optionGroup);
		}

		/**
		 * 期望参数集构建函数。
		 * 该构建会扫描Jar包中所有使用OptionAnnotation注解的参数项
		 * 并将对应参数项解析放入至期望参数集中。
		 *
		 * @return 期望参数集
		 * @throws IOException            异常
		 * @throws ClassNotFoundException 异常
		 */
		public Options build() throws IOException, ClassNotFoundException {
			Options instance = new Options();
			options.forEach(opt -> {
				try {
					instance.addOption(opt);
				} catch (MultiOptionGroupException e) {
					throw new IllegalStateException(e);
				}
			});
			optionGroups.forEach(instance::addOptionGroup);
			loadAnnotation(instance);
			return instance;
		}

		private void loadAnnotation(Options instance) throws IOException, ClassNotFoundException {
			AnnotationRegistration registration = AnnotationRegistration.getInstance();
			Iterator<Map.Entry<Annotation, Class<?>>> iterator = registration.iterator(annotation -> annotation instanceof OptionAnnotation);
			while (iterator.hasNext()) {
				Map.Entry<Annotation, Class<?>> entry = iterator.next();
				OptionAnnotation annotation = (OptionAnnotation) entry.getKey();
				try {
					// 加入内置参数项
					instance.addOption(HELP_OPTION);
					instance.addOption(VERSION_OPTION);
					// 扫描检查是否时值绑定类型，是否时可选参数值
					boolean isValueBind = false;
					boolean isOptionalArg = false;
					for (Annotation obtain : entry.getValue().getAnnotations()) {
						if (obtain instanceof OptionBindType && ((OptionBindType) obtain).value().equals(OptionBindTypeEnum.VALUE_BIND)) {
							isValueBind = true;
						}
						if (obtain instanceof OptionalArgument) {
							isOptionalArg = true;
						}
					}
					// 获取参数项所在的参数组
					OptionGroup optionGroup = instance.getOptionGroup(group -> group.getGroupName().equals(annotation.groupName()),
							annotation.groupName());
					// 获取参数项
					final Option newOpt = Option.builder(annotation.opt()).addLongOpt(annotation.longOpt())
							.hasArg(annotation.hasArgs()).isRequired(annotation.isRequired()).numOfArg(annotation.numOfArgs())
							.addDesc(annotation.desc()).addValueSep(annotation.valueSeq()).isOptionalArg(isOptionalArg).build();
					Option option = optionGroup.getFirstOption(opt -> opt.getOpt().equals(newOpt.getOpt()), newOpt);
					// 设置绑定函数
					option.addBindClass(isValueBind ? annotation.bindValue() : annotation.opt(), entry.getValue());
					// 保存解析完成的参数项
					optionGroup.addOption(option);
					instance.addOptionGroup(optionGroup);
				} catch (MultiOptionGroupException e) {
					throw new IllegalStateException(e);
				}
			}
		}

	}

}
