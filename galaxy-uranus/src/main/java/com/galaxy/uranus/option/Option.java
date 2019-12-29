package com.galaxy.uranus.option;

import com.galaxy.uranus.utils.OptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 参数项对象
 * 该对象用于描述参数的各中属性
 * 用于保存从命令行传入的参数相关的内容
 * @Date : Create in 22:09 2019/12/18
 * @Modified By:
 */
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

	public char getValueSep() {
		return valueSep;
	}

	public boolean isHasArgs() {
		return hasArgs;
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

	public void setOptionGroup(OptionGroup optionGroup) {
		this.optionGroup = optionGroup;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Option option = (Option) o;
		return Objects.equals(opt, option.opt) &&
				Objects.equals(longOpt, option.longOpt) &&
				Objects.equals(values, ((Option) o).values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(opt, longOpt, isRequired, values, valueSep, hasArgs, numOfArgs, hasIpt, optionGroup, desc);
	}

	public static OptionBuilder builder(String opt) {
		return new OptionBuilder(opt);
	}

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

		public OptionBuilder(String opt) {
			this.opt = opt;
			this.values = new ArrayList<>();
		}

		public OptionBuilder addLongOpt(String longOpt) {
			this.longOpt = longOpt;
			return this;
		}

		public OptionBuilder addValue(String value) {
			this.values.add(value);
			return this;
		}

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

		public OptionBuilder addDesc(String desc) {
			this.desc = desc;
			return this;
		}

		public Option build() {
			Option option = new Option();
			option.opt = this.opt;
			option.longOpt = this.longOpt;
			option.hasIpt = this.hasIpt;
			option.isRequired = this.isRequired;
			option.numOfArgs = this.numOfArgs;
			option.hasArgs = this.hasArgs;
			option.values = this.values;
			option.valueSep = this.valueSep;
			option.desc = this.desc;
			option.optionGroup = this.optionGroup;
			return option;
		}
	}
}
