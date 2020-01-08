import com.galaxy.uranus.CommandLine;
import com.galaxy.uranus.examples.OptionalValueBindFunc;
import com.galaxy.uranus.examples.TypeBindFunc;
import com.galaxy.uranus.examples.ValueBindFunc;
import com.galaxy.uranus.exception.UranusException;
import com.galaxy.uranus.option.Option;
import com.galaxy.uranus.option.OptionGroup;
import com.galaxy.uranus.option.Options;
import com.galaxy.uranus.parser.UranusParser;
import com.galaxy.uranus.utils.OptionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:15 2019/12/30
 * @Modified By:
 */
public class OptionsTest {

	/**
	 * 测试Builder构建器功能正确
	 */
	@Test
	public void testOne() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
		Option expect1 = Option.builder("galaxy").addLongOpt("galaxy_test").build();
		Option expect2 = Option.builder("uranus").addLongOpt("uranus_test").build();
		Option expect3 = Option.builder("opt").addLongOpt("optional").build();
		Options options = Options.builder().build();
		Option option1 = options.getOption(opt -> opt.getOpt().equals("galaxy"));
		Assert.assertEquals(expect1, option1);
		Option option2 = options.getOption(opt -> opt.getOpt().equals("uranus"));
		Assert.assertEquals(expect2, option2);
		Option option3 = options.getOption(opt -> opt.getOpt().equals("opt"));
		Assert.assertEquals(expect3, option3);
		Assert.assertTrue(option1.getBindFunc() instanceof TypeBindFunc);
		option2.addValue("2019");
		Assert.assertTrue(option2.getBindFunc() instanceof ValueBindFunc);
		option3.addValue("2017");
		Assert.assertTrue(option3.getBindFunc() instanceof OptionalValueBindFunc);
	}

	/**
	 * 测试默认状态下参数解析是否正常
	 */
	@Test
	public void testTwo() throws IOException, ClassNotFoundException, UranusException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Options options = Options.builder().build();
		UranusParser parser = new UranusParser();
		CommandLine commandLine = parser.parse(options, new String[]{"-opt"}, OptionUtils.getPropertiesFromOptionAnnotation());
		List<OptionGroup> optionGroups = commandLine.getOptionGroups();
		Assert.assertEquals(1, optionGroups.size());
		OptionGroup optionGroup = optionGroups.get(0);
		Assert.assertEquals(1, optionGroup.getGroupSize());
		Option option = optionGroup.getOption(opt -> opt.getOpt().equals("opt"));
		Assert.assertEquals(Option.builder("opt").addLongOpt("optional").hasArgs(true)
				.addNumOfArgs(2).addValue("2017").addValue("2018").build(), option);
		Assert.assertEquals(OptionalValueBindFunc.class, option.getBindFunc().getClass());
	}

}