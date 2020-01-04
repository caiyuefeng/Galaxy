package com.galaxy.uranus.option;

import com.galaxy.uranus.utils.OptionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 参数项对象
 * 该对象用于描述参数的各中属性
 * 用于保存从命令行传入的参数相关的内容
 * @Date : Create in 22:09 2019/12/18
 * @Modified By:
 */
@SuppressWarnings("unused")
public class Option {

	/**
	 * 短参名
	 */
	private String opt;

	/**
	 * 长参名
	 */
	private String longOpt;

	/**
	 * 命令行是否必须输入
	 */
	private boolean isRequired;

	/**
	 * 参数值集合
	 */
	private List<String> values;

	/**
	 * 参数值分隔符
	 */
	private char valueSep = ' ';

	/**
	 * 是否接受参数值
	 */
	private boolean hasArgs;

	/**
	 * 参数值个数
	 */
	private int numOfArgs;

	/**
	 * 命令行是否已经输入
	 */
	private boolean hasIpt;

	/**
	 * 所属参数组
	 */
	private OptionGroup optionGroup;

	/**
	 * 参数描述
	 */
	private String desc;

	/**
	 * 绑定的功能类
	 */
	private Map<String, Class<?>> bindClazz;

	private Option() {
		this.values = new ArrayList<>();
	}

	public String getOpt() {
		return opt;
	}

	public String getLongOpt() {
		return longOpt;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public List<String> getValues() {
		return values;
	}

	public int getNumOfArgs() {
		return numOfArgs;
	}

	public boolean hasIpt() {
		return hasIpt;
	}

	public void setHasIpt(boolean hasIpt) {
		this.hasIpt = hasIpt;
	}

	/**
	 * 该参数项是否接受参数
	 * 当前仅当 参数项已经从命令行输入，接受参数值，且接受的参数值小于所需的参数值个数
	 *
	 * @return 标志
	 */
	public boolean acceptArgs() {
		return hasIpt && hasArgs && values.size() < numOfArgs;
	}

	public OptionGroup getOptionGroup() {
		return optionGroup;
	}

	public String getDesc() {
		return desc;
	}

	public Object getBindFunc() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Class<?> clazz = bindClazz.get(opt);
		if (clazz == null && !values.isEmpty()) {
			for (String value : values) {
				clazz = bindClazz.get(value);
				if (clazz != null) {
					break;
				}
			}
		}
		if (clazz != null) {
			Constructor constructor = clazz.getConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		return null;
	}

	/**
	 * 向参数项中加入参数值
	 *
	 * @param value 参数值
	 */
	public void addValue(String value) {
		int index = value.indexOf(valueSep);
		while (index != -1) {
			values.add(OptionUtils.formatOptionVal(value.substring(0, index)));
			value = value.substring(index + 1);
			index = value.indexOf(valueSep);
		}
		if (value.length() > 0) {
			values.add(OptionUtils.formatOptionVal(value));
		}
	}

	void setOptionGroup(OptionGroup optionGroup) {
		this.optionGroup = optionGroup;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Option option = (Option) o;
		boolean check = values.containsAll(option.values) && option.values.containsAll(values);
		return Objects.equals(opt, option.opt) &&
				Objects.equals(longOpt, option.longOpt) &&
				check;
	}

	@Override
	public int hashCode() {
		return Objects.hash(opt, longOpt, isRequired, values, valueSep, hasArgs, numOfArgs, hasIpt, optionGroup, desc);
	}

	public static OptionBuilder builder(String opt) {
		return new OptionBuilder(opt);
	}

	@SuppressWarnings("WeakerAccess")
	public static class OptionBuilder {

		/**
		 * 短参名
		 */
		private String opt;

		/**
		 * 长参名
		 */
		private String longOpt;

		/**
		 * 命令行是否必须输入
		 */
		private boolean isRequired;

		/**
		 * 参数值集合
		 */
		private List<String> values;

		/**
		 * 参数值分隔符
		 */
		private char valueSep;

		/**
		 * 是否接受参数值
		 */
		private boolean hasArgs = false;

		/**
		 * 参数值个数
		 */
		private int numOfArgs = -1;

		/**
		 * 所属参数组
		 */
		private OptionGroup optionGroup;

		/**
		 * 参数描述
		 */
		private String desc;

		/**
		 * 绑定的类
		 */
		private Map<String, Class<?>> bindClazz;

		OptionBuilder(String opt) {
			this.opt = opt;
			this.values = new ArrayList<>();
			this.bindClazz = new HashMap<>();
		}

		public OptionBuilder addLongOpt(String longOpt) {
			this.longOpt = longOpt;
			return this;
		}

		public OptionBuilder addValue(String value) {
			this.values.add(value);
			return this;
		}

		@SuppressWarnings("WeakerAccess")
		public OptionBuilder addValueSep(char valueSep) {
			this.valueSep = valueSep;
			return this;
		}

		public OptionBuilder isRequired(boolean isRequired) {
			this.isRequired = isRequired;
			return this;
		}

		public OptionBuilder hasArgs(boolean hasArgs) {
			this.hasArgs = hasArgs;
			return this;
		}

		public OptionBuilder addNumOfArgs(int numOfArgs) {
			this.numOfArgs = numOfArgs;
			return this;
		}

		public OptionBuilder addOptionGroup(OptionGroup group) {
			this.optionGroup = group;
			return this;
		}

		@SuppressWarnings("WeakerAccess")
		public OptionBuilder addDesc(String desc) {
			this.desc = desc;
			return this;
		}

		@SuppressWarnings("WeakerAccess")
		public OptionBuilder addBindClass(String key, Class<?> clazz) {
			this.bindClazz.put(key, clazz);
			return this;
		}

		public Option build() {
			Option option = new Option();
			option.opt = this.opt;
			option.longOpt = this.longOpt;
			option.isRequired = this.isRequired;
			option.numOfArgs = this.numOfArgs;
			option.hasArgs = this.hasArgs;
			option.values = this.values;
			option.valueSep = this.valueSep;
			option.desc = this.desc;
			option.optionGroup = this.optionGroup;
			option.bindClazz = this.bindClazz;
			return option;
		}
	}
}
