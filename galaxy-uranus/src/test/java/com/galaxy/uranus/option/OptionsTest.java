package com.galaxy.uranus.option;

import com.galaxy.uranus.examples.TypeBindFunc;
import com.galaxy.uranus.examples.ValueBindFunc;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

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
	public void testOne() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Option expect1 = Option.builder("galaxy").addLongOpt("galaxy_test").build();
		Option expect2 = Option.builder("uranus").addLongOpt("uranus_test").build();
		Options options = Options.builder().build();
		Option option1 = options.getOption(opt -> opt.getOpt().equals("galaxy"));
		Assert.assertEquals(expect1,option1);
		Option option2 = options.getOption(opt -> opt.getOpt().equals("uranus"));
		Assert.assertEquals(expect2, option2);
		Assert.assertTrue(option1.getBindFunc() instanceof TypeBindFunc);
		option2.addValue("2019");
		Assert.assertTrue(option2.getBindFunc() instanceof ValueBindFunc);
	}

}