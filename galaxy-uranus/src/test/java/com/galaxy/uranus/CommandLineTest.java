package com.galaxy.uranus;

import com.galaxy.uranus.exception.MultiOptionGroupException;
import com.galaxy.uranus.option.Option;
import com.galaxy.uranus.option.OptionGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 21:13 2020/1/9
 *
 */
public class CommandLineTest {


	/**
	 * 测试 get(String groupName)方法
	 * 获取命令行中存在对应参数组名的参数组
	 */
	@Test
	public void testGetMethod1() throws MultiOptionGroupException {
		CommandLine commandLine = new CommandLine();
		OptionGroup group = new OptionGroup("test");
		group.addOption(Option.builder("m").build());
		commandLine.add(group);
		Assert.assertEquals(1, commandLine.getOptionGroups().size());
		OptionGroup expect = new OptionGroup("test");
		expect.addOption(Option.builder("m").build());
		Assert.assertEquals(expect, commandLine.get("test"));
	}

	/**
	 * 测试 get(Option option)方法
	 * 获取命令行中存在对应参数组名的参数组
	 */
	@Test
	public void testGetMethod2() throws MultiOptionGroupException {
		CommandLine commandLine = new CommandLine();
		OptionGroup group = new OptionGroup("test");
		group.addOption(Option.builder("m").build());
		commandLine.add(group);
		Assert.assertEquals(1, commandLine.getOptionGroups().size());
		OptionGroup expect = new OptionGroup("test");
		expect.addOption(Option.builder("m").build());
		Assert.assertEquals(expect, commandLine.get(Option.builder("m").build()));
	}

	/**
	 * 测试 get(Option option)方法
	 * 获取命令行中存在对应参数组名的参数组
	 */
	@Test
	public void testGetMethod3() throws MultiOptionGroupException {
		CommandLine commandLine = new CommandLine();
		OptionGroup group = new OptionGroup("test");
		group.addOption(Option.builder("m").build());
		group.addOption(Option.builder("v").build());
		group.addOption(Option.builder("h").build());
		commandLine.add(group);
		Assert.assertEquals(1, commandLine.getOptionGroups().size());
		List<Option> options = new ArrayList<>();
		options.add(Option.builder("m").build());
		Assert.assertEquals(options, commandLine.get((groupName, option) -> groupName.equals("test") && option.getOpt().equals("m")));
	}

	/**
	 * 测试 get(Option option)方法
	 * 不同参数组中必须输入的参数项
	 */
	@Test
	public void testGetMethod4() throws MultiOptionGroupException {
		CommandLine commandLine = new CommandLine();
		OptionGroup group1 = new OptionGroup("test");
		group1.addOption(Option.builder("m").isRequired(true).build());
		group1.addOption(Option.builder("v").build());
		OptionGroup group2 = new OptionGroup("parse");
		group2.addOption(Option.builder("h").build());
		group2.addOption(Option.builder("g").isRequired(true).build());
		commandLine.add(group1);
		commandLine.add(group2);
		Assert.assertEquals(2, commandLine.getOptionGroups().size());
		List<Option> options = new ArrayList<>();
		options.add(Option.builder("m").isRequired(true).build());
		options.add(Option.builder("g").isRequired(true).build());
		Assert.assertEquals(options, commandLine.get((groupName, option) -> option.isRequired()));
	}
}