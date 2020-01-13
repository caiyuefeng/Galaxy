package com.galaxy.uranus.option;

import com.galaxy.uranus.utils.OptionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 参数项。
 * 该类用于描述参数的各种属性，用于保存从命令行传入的参数值。
 * <p>
 * 参数项不可以通过new关键字进行新建对象，只能通过OptionBuilder构建器进行构建。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 22:09 2019/12/18
 * @see OptionBuilder
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
	 * 参数值分隔符。
	 * 该字段默认值未单个空格，通常从命令行输入参数时，默认会以空白符作为输入参数
	 * 的分隔符，在未使用转义字符(\)的情况下，所有输入参数均不会包含空格，因此默认
	 * 情况下不会切分参数值。
	 */
	private char valueSep = ' ';

	/**
	 * 是否接受参数值
	 */
	private boolean hasArg;

	/**
	 * 参数值个数
	 */
	private int numOfArg;

	/**
	 * 参数项输入标识
	 */
	private boolean hasIpt;

	/**
	 * 所属参数组
	 */
	private OptionGroup optionGroup;

	/**
	 * 参数项描述
	 */
	private String desc;

	/**
	 * 参数值是否可选填
	 */
	private boolean isOptionalArg;

	/**
	 * 绑定的功能类。
	 * 该缓存用于保存参数项绑定的功能类，绑定的情况如下：
	 * <ul>
	 *     <li>如果参数项是类型绑定，则key值则为短参名;</li>
	 *     <li>如果参数项时，则key为对应的参数值</li>
	 * </ul>
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

	public int getNumOfArg() {
		return numOfArg;
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
		return hasIpt && hasArg && values.size() < numOfArg;
	}

	/**
	 * 检查该参数项是否已经输入完毕。
	 * 参数项输入完毕满足以下两种条件任一即可：
	 * <ul>
	 *     <li>参数项不再接受参数；</li>
	 *     <li>参数项的已经输入且参数项值未可选参数值。</li>
	 * </ul>
	 *
	 * @return 检查结果
	 */
	public boolean isComplete() {
		return !acceptArgs() || (hasIpt && isOptionalArg);
	}

	public OptionGroup getOptionGroup() {
		return optionGroup;
	}

	public String getDesc() {
		return desc;
	}

	public boolean isOptionalArg() {
		return isOptionalArg;
	}

	/**
	 * 获取绑定的功能类实例。
	 * 获取实例时，默认会先通过短参名获取，如果未获取到
	 * 则使用参数值进行获取，如果还未获取到则返回NULL
	 *
	 * @return 实例对象
	 * @throws NoSuchMethodException     无方法异常
	 * @throws IllegalAccessException    非法权限异常
	 * @throws InvocationTargetException 反射异常
	 * @throws InstantiationException    反射异常
	 */
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

	/**
	 * 加入绑定函数。
	 * 如果参数项时值绑定时，就可能会通过不同的值绑定多个功能类。
	 *
	 * @param bindValue 绑定的参数值或参数项
	 * @param clazz     函数类签名
	 */
	void addBindClass(String bindValue, Class<?> clazz) {
		this.bindClazz.put(bindValue, clazz);
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
		return Objects.hash(opt, longOpt, isRequired, values, valueSep, hasArg, numOfArg, hasIpt, optionGroup, desc);
	}

	public static OptionBuilder builder(String opt) {
		return new OptionBuilder(opt);
	}

	/**
	 * 参数项构建器。
	 * 构建器使用方法如下：
	 * <pre>
	 *     <code>
	 *      OptionBuilder builder = new OptionBuilder("p").longOpt("process").build();
	 *     </code>
	 * </pre>
	 */
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
		private int numOfArg = -1;

		/**
		 * 所属参数组
		 */
		private OptionGroup optionGroup;

		/**
		 * 参数描述
		 */
		private String desc;

		/**
		 * 可选参数值标志
		 */
		private boolean isOptionalArg;

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

		public OptionBuilder hasArg(boolean hasArgs) {
			this.hasArgs = hasArgs;
			return this;
		}

		public OptionBuilder numOfArg(int numOfArg) {
			this.numOfArg = numOfArg;
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

		public OptionBuilder isOptionalArg(boolean isOptionalArg) {
			this.isOptionalArg = isOptionalArg;
			return this;
		}

		public OptionBuilder addBindClass(String key, Class<?> clazz) {
			this.bindClazz.put(key, clazz);
			return this;
		}

		public Option build() {
			Option option = new Option();
			option.opt = this.opt;
			option.longOpt = this.longOpt;
			option.isRequired = this.isRequired;
			option.numOfArg = this.numOfArg;
			option.hasArg = this.hasArgs;
			option.values = this.values;
			option.valueSep = this.valueSep;
			option.desc = this.desc;
			option.optionGroup = this.optionGroup;
			option.bindClazz = this.bindClazz;
			option.isOptionalArg = this.isOptionalArg;
			return option;
		}
	}
}
