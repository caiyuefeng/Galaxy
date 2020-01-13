package com.galaxy.uranus.parser;

import com.galaxy.uranus.CommandLine;
import com.galaxy.uranus.exception.UnCompleteException;
import com.galaxy.uranus.exception.UranusException;
import com.galaxy.uranus.option.Option;
import com.galaxy.uranus.option.OptionGroup;
import com.galaxy.uranus.option.Options;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 19:32 2019/12/28
 *
 */
public class UranusParserTest {

	/**
	 * 短参测试
	 * 参数非必须 且无参数输入
	 */
	@Test
	public void testOne() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").addLongOpt("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(0, optionGroups.size());
	}

	/**
	 * 短参测试
	 * 单参数项非必须 且命令行输入
	 */
	@Test
	public void testTwo() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").addLongOpt("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-p"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Option expect = Option.builder("p").addLongOpt("path").build();
		Assert.assertEquals(expect, commandLine.get("default").getOption(opt -> true));
	}

	/**
	 * 短参测试
	 * 单参数项必须输入 且命令行未输入
	 */
	@Test(expected = UnCompleteException.class)
	public void testThree() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").isRequired(true).addLongOpt("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(0, optionGroups.size());
	}

	/**
	 * 短参测试
	 * -K
	 * 单参数项必须输入 且命令行输入
	 */
	@Test
	public void testFour() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").isRequired(true).addLongOpt("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-p"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Option expect = Option.builder("p").addLongOpt("path").build();
		Assert.assertEquals(expect, commandLine.get("default").getOption(opt -> true));
	}

	/**
	 * 短参测试
	 * -K
	 * 单参数项必须输入 且命令行输入错误参数
	 */
	@Test(expected = UnCompleteException.class)
	public void testFive() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").isRequired(true).addLongOpt("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-V"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(0, optionGroups.size());
		Assert.assertArrayEquals(Collections.singleton("V").toArray(), commandLine.getUnknownToken().toArray());
	}

	/**
	 * 短参测试
	 * -K
	 * 单参数项必须输入 且命令行输入正确参数
	 */
	@Test
	public void testSix() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").isRequired(true).addLongOpt("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-p"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Assert.assertEquals(0, commandLine.getUnknownToken().size());
		Option expect = Option.builder("p").addLongOpt("path").build();
		Assert.assertEquals(expect, commandLine.get("default").getOption(opt -> true));
	}

	/**
	 * 短参测试
	 * -K
	 * 单参数项非必须输入 且命令行输入错误参数
	 */
	@Test
	public void testSeven() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").addLongOpt("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-V"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(0, optionGroups.size());
		Assert.assertEquals(1, commandLine.getUnknownToken().size());
		Assert.assertArrayEquals(Collections.singleton("V").toArray(), commandLine.getUnknownToken().toArray());
	}

	/**
	 * 短参测试
	 * -K
	 * 单参数项非必须输入 接受两个参数 命令行输入一个参数
	 */
	@Test(expected = UnCompleteException.class)
	public void testSevenB() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").addLongOpt("path").hasArg(true).numOfArg(2).build());
		parser.parse(options, new String[]{"-p", "path"});
	}

	/**
	 * 短参测试
	 * -Key
	 * 单参数项非必须输入 且命令行输入正确参数
	 */
	@Test
	public void testEight() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-path"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Option expect = Option.builder("path").build();
		Assert.assertEquals(expect, commandLine.get("default").getOption(opt -> true));
	}

	/**
	 * 短参测试
	 * -DKey=V
	 * 单参数项非必须输入 且命令行输入正确参数
	 */
	@Test
	public void testNine() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-Dpath=input"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Option expect = Option.builder("path").addValue("input").build();
		Assert.assertEquals(expect, commandLine.get("default").getOption(opt -> true));
	}

	/**
	 * 短参测试
	 * -K V
	 * 单参数项非必须输入 有一个参数值 且命令行输入正确参数 和参数值
	 */
	@Test
	public void testTen() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").hasArg(true).numOfArg(1).build());
		CommandLine commandLine = parser.parse(options, new String[]{"-p", "input"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Option expect = Option.builder("p").addValue("input").build();
		Assert.assertEquals(expect, commandLine.get("default").getOption(opt -> true));
	}

	/**
	 * 短参测试
	 * -Key V
	 * 单参数项非必须输入 有一个参数值 且命令行输入正确参数 和参数值
	 */
	@Test
	public void test11() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("path").hasArg(true).numOfArg(1).build());
		CommandLine commandLine = parser.parse(options, new String[]{"-path", "input"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Assert.assertEquals(Option.builder("path").addValue("input").build(),
				commandLine.get("default").getOption(opt -> true));
	}

	/**
	 * 短参测试
	 * -K V
	 * 单参数项非必须输入 有一个参数值 且命令行输入正确参数 未输入参数值
	 */
	@Test(expected = UnCompleteException.class)
	public void test12() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").hasArg(true).numOfArg(1).build());
		parser.parse(options, new String[]{"-p"});
	}

	/**
	 * 短参测试
	 * -Key V
	 * 单参数项非必须输入 有一个参数值 且命令行输入正确参数 未输入参数值
	 */
	@Test(expected = UnCompleteException.class)
	public void test13() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("path").hasArg(true).numOfArg(1).build());
		parser.parse(options, new String[]{"-path"});
	}

	/**
	 * 短参测试
	 * -Key
	 * 单参数项非必须输入 无参数值 且命令行输入错误参数
	 */
	@Test
	public void test14() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("path").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-p"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(0, optionGroups.size());
		Assert.assertEquals(1, commandLine.getUnknownToken().size());
		Assert.assertArrayEquals(Collections.singleton("p").toArray(), commandLine.getUnknownToken().toArray());
	}

	/**
	 * 短参测试
	 * -Key
	 * 单参数项必须输入 无参数值 且命令行输入错误参数
	 */
	@Test(expected = UnCompleteException.class)
	public void test15() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("path").isRequired(true).build());
		CommandLine commandLine = parser.parse(options, new String[]{"-p"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(0, optionGroups.size());
		Assert.assertEquals(1, commandLine.getUnknownToken().size());
		Assert.assertArrayEquals(Collections.singleton("p").toArray(), commandLine.getUnknownToken().toArray());
	}

	/**
	 * 短参测试
	 * -DKey=V
	 * 单参数项非必须输入 有一个参数值 且命令行输入错误
	 */
	@Test(expected = IllegalStateException.class)
	public void test16() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("path").hasArg(true).numOfArg(1).build());
		parser.parse(options, new String[]{"-Dp=v"});
	}

	/**
	 * 短参测试
	 * -K1K2K3
	 * 单参数项非必须输入 最后一项有一个参数值 且命令行输入一个参数值
	 */
	@Test
	public void test17() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").hasArg(true).numOfArg(1).build());
		options.addOption(Option.builder("j").build());
		options.addOption(Option.builder("u").build());
		CommandLine commandLine = parser.parse(options, new String[]{"-jup", "input"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Assert.assertEquals(3, optionGroups.get(0).getGroupSize());
		Assert.assertEquals(Option.builder("p").addValue("input").build(),
				commandLine.get("default").getOption(opt -> opt.getOpt().equals("p")));
		Assert.assertEquals(Option.builder("j").build(),
				commandLine.get("default").getOption(opt -> opt.getOpt().equals("j")));
		Assert.assertEquals(Option.builder("u").build(),
				commandLine.get("default").getOption(opt -> opt.getOpt().equals("u")));
	}

	/**
	 * 短参测试
	 * -K1K2K3
	 * 单参数项非必须输入 多个参数接受参数值
	 */
	@Test(expected = IllegalStateException.class)
	public void test18() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").hasArg(true).numOfArg(1).build());
		options.addOption(Option.builder("j").hasArg(true).numOfArg(1).build());
		options.addOption(Option.builder("u").build());
		parser.parse(options, new String[]{"-jup", "input"});
	}

	/**
	 * 长参测试
	 * --K
	 * --Key
	 * --Dkey=V
	 * 单参数项非必须输入 且命令行输入正确参数
	 */
	@Test
	public void test19() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("i").addLongOpt("j").build());
		options.addOption(Option.builder("p").addLongOpt("path").build());
		options.addOption(Option.builder("u").addLongOpt("upload").hasArg(true).numOfArg(1).build());
		CommandLine commandLine = parser.parse(options, new String[]{"--j", "--path", "--Dupload=v"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Assert.assertEquals(3, optionGroups.get(0).getGroupSize());
		Assert.assertEquals(Option.builder("i").addLongOpt("j").build(),
				commandLine.get("default").getOption(opt -> opt.getOpt().equals("i")));
		Assert.assertEquals(Option.builder("p").addLongOpt("path").build(),
				commandLine.get("default").getOption(opt -> opt.getOpt().equals("p")));
		Assert.assertEquals(Option.builder("u").addLongOpt("upload").addValue("v").build(),
				commandLine.get("default").getOption(opt -> opt.getOpt().equals("u")));
	}

	/**
	 * 长参测试
	 * --K
	 * 单参数项非必须输入 接受一个参数 且命令行未输入参数
	 */
	@Test(expected = UnCompleteException.class)
	public void test20() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		options.addOption(Option.builder("p").addLongOpt("path").hasArg(true).numOfArg(1).build());
		parser.parse(options, new String[]{"--path"});
	}

	/**
	 * 参数组测试
	 * 参数组必填 多个参数项非必填 命令行输入一个参数项
	 */
	@Test
	public void test21() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		OptionGroup group1 = new OptionGroup("path", true);
		group1.addOption(Option.builder("p").addLongOpt("path").hasArg(true).numOfArg(1).build());
		group1.addOption(Option.builder("j").addLongOpt("job").hasArg(true).numOfArg(1).build());
		options.addOptionGroup(group1);
		CommandLine commandLine = parser.parse(options, new String[]{"--path", "input"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Assert.assertEquals(1, optionGroups.get(0).getGroupSize());
		Assert.assertEquals(0, commandLine.getUnknownToken().size());
		Assert.assertEquals(Option.builder("p").addLongOpt("path").addValue("input").build(),
				commandLine.get("path").getOption(opt -> opt.getOpt().equals("p")));
	}

	/**
	 * 参数组测试
	 * 参数组必填 多个参数项非必填 参数未填入
	 */
	@Test(expected = UnCompleteException.class)
	public void test22() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		OptionGroup group1 = new OptionGroup("path", true);
		group1.addOption(Option.builder("p").addLongOpt("path").hasArg(true).numOfArg(1).build());
		group1.addOption(Option.builder("j").addLongOpt("job").hasArg(true).numOfArg(1).build());
		options.addOptionGroup(group1);
		parser.parse(options, new String[]{});
	}

	/**
	 * 混合参数
	 * 两个参数组
	 * A组 非必填 5个短参数(其中一个必填参数接受 两个非必填参数)
	 * B组 必填  长参测试 接受多个参数
	 */
	@Test
	public void test23() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		OptionGroup group1 = new OptionGroup("path");
		group1.addOption(Option.builder("p").addLongOpt("path").isRequired(true)
				.hasArg(true).numOfArg(1).build());
		group1.addOption(Option.builder("j").addLongOpt("job").build());
		group1.addOption(Option.builder("u").addLongOpt("upload").build());
		group1.addOption(Option.builder("i").addLongOpt("id").build());
		options.addOptionGroup(group1);
		OptionGroup group2 = new OptionGroup("data", true);
		group2.addOption(Option.builder("tp").addLongOpt("testPath")
				.hasArg(true).numOfArg(1).build());
		group2.addOption(Option.builder("ep").addLongOpt("expectPath").hasArg(true).numOfArg(1).build());
		group2.addOption(Option.builder("m").addLongOpt("mode").build());
		options.addOptionGroup(group2);
		CommandLine commandLine = parser.parse(options, new String[]{"-p", "input", "-ju", "--testPath", "input1", "--expectPath", "input2"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(2, optionGroups.size());
		Assert.assertEquals(3, commandLine.get("path").getGroupSize());
		Assert.assertEquals(2, commandLine.get("data").getGroupSize());
		Assert.assertEquals(0, commandLine.getUnknownToken().size());
		Assert.assertEquals(Option.builder("p").addLongOpt("path").addValue("input").build(),
				commandLine.get("path").getOption(opt -> opt.getOpt().equals("p")));
		Assert.assertEquals(Option.builder("j").addLongOpt("job").build(),
				commandLine.get("path").getOption(opt -> opt.getOpt().equals("j")));
		Assert.assertEquals(Option.builder("u").addLongOpt("upload").build(),
				commandLine.get("path").getOption(opt -> opt.getOpt().equals("u")));
		Assert.assertEquals(Option.builder("tp").addLongOpt("testPath").addValue("input1").build(),
				commandLine.get("data").getOption(opt -> opt.getOpt().equals("tp")));
		Assert.assertEquals(Option.builder("ep").addLongOpt("expectPath").addValue("input2").build(),
				commandLine.get("data").getOption(opt -> opt.getOpt().equals("ep")));
	}

	/**
	 * 可选参数测试
	 * 预期参数项一为可选参数 命令行正常输入参数值
	 * 预期参数项二为可选参数 命令行未输入参数值
	 * 设置不接受-或--的参数值
	 * 结果两个参数项都保留
	 */
	@Test
	public void test24() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		OptionGroup group1 = new OptionGroup("group1");
		group1.addOption(Option.builder("p").addLongOpt("path").isRequired(true)
				.hasArg(true).numOfArg(1).isOptionalArg(true).build());
		group1.addOption(Option.builder("j").addLongOpt("job").hasArg(true)
				.numOfArg(1).isOptionalArg(true).build());
		options.addOptionGroup(group1);
		CommandLine commandLine = parser.parse(options, new String[]{"-j", "-p", "input"}, new HashMap<>(), false);
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Assert.assertEquals(2, commandLine.get("group1").getGroupSize());
		Assert.assertEquals(0, commandLine.getUnknownToken().size());
		Assert.assertEquals(Option.builder("p").addLongOpt("path").addValue("input").build(),
				commandLine.get("group1").getOption(opt -> opt.getOpt().equals("p")));
		Assert.assertEquals(Option.builder("j").addLongOpt("job").build(),
				commandLine.get("group1").getOption(opt -> opt.getOpt().equals("j")));
	}

	/**
	 * 可选参数测试
	 * 预期参数项一为可选参数 命令行正常输入参数值
	 * 预期参数项二为可选参数 命令行未输入参数值
	 * 接受-或--的参数值
	 * 抛异常
	 */
	@Test(expected = UnCompleteException.class)
	public void test25() throws UranusException {
		UranusParser parser = new UranusParser();
		Options options = new Options();
		OptionGroup group1 = new OptionGroup("group1");
		group1.addOption(Option.builder("p").addLongOpt("path").isRequired(true)
				.hasArg(true).numOfArg(1).isOptionalArg(true).build());
		group1.addOption(Option.builder("j").addLongOpt("job").hasArg(true)
				.numOfArg(1).isOptionalArg(true).build());
		options.addOptionGroup(group1);
		CommandLine commandLine = parser.parse(options, new String[]{"-j", "-p", "input"});
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		Assert.assertEquals(2, commandLine.get("group1").getGroupSize());
		Assert.assertEquals(0, commandLine.getUnknownToken().size());
		Assert.assertEquals(Option.builder("p").addLongOpt("path").addValue("input").build(),
				commandLine.get("group1").getOption(opt -> opt.getOpt().equals("p")));
		Assert.assertEquals(Option.builder("j").addLongOpt("job").build(),
				commandLine.get("group1").getOption(opt -> opt.getOpt().equals("j")));
	}
}