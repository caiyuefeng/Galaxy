package com.galaxy.uranus;

import com.galaxy.uranus.exception.MultiOptionGroupException;
import com.galaxy.uranus.option.Option;
import com.galaxy.uranus.option.Options;
import org.junit.Test;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 23:15 2020/1/9
 *
 */
public class HelpInfoFormatTest {

	@Test
	public void testOne() throws MultiOptionGroupException {
		Options options = new Options();
		options.addOption(Option.builder("m").addLongOpt("mode").addDesc("执行模式").build());
		options.addOption(Option.builder("p").addLongOpt("process").addDesc("处理进程名").build());
		options.addOption(Option.builder("j").addLongOpt("journal").addDesc("日记").build());
		options.addOption(Option.builder("l").addLongOpt("LevelOfIllegal").addDesc("违法违章等级").build());
		HelpInfoFormat format = new HelpInfoFormat(options);
	}

	@Test
	public void testTwo() throws MultiOptionGroupException {
		Options options = new Options();
		options.addOption(Option.builder("m").addLongOpt("mode").addDesc("执行模式").build());
		options.addOption(Option.builder("p").addLongOpt("process").addDesc("处理进程名").build());
		options.addOption(Option.builder("j").addLongOpt("journal").addDesc("日记").build());
		options.addOption(Option.builder("l").addLongOpt("LevelOfIllegal").addDesc("违法违章等级").build());
		HelpInfoFormat format = new HelpInfoFormat("process -[m|p|j|l] -[option_value]", "  process -m energy  启用节能模式\n  precess -process backstage  执行后台进程", options);
	}
}