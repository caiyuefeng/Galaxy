package com.galaxy.uranus.examples;

import com.galaxy.uranus.annotation.OptionAnnotation;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:17 2019/12/30
 * @Modified By:
 */
@OptionAnnotation(opt = "galaxy", longOpt = "galaxy_test",
		value = "2019", valueSeq = '-', hasArgs = true, numOfArgs = 1,
		desc = "时间", groupName = "Test")
public class TypeBindFunc {

}
