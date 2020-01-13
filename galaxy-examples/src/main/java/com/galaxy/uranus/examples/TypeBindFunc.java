package com.galaxy.uranus.examples;

import com.galaxy.uranus.annotation.OptionAnnotation;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 21:17 2019/12/30
 *
 */
@OptionAnnotation(opt = "galaxy", longOpt = "galaxy_test",
		bindValue = "2019", valueSeq = '-', hasArgs = true, numOfArgs = 1,
		desc = "时间", groupName = "Test")
public class TypeBindFunc {

}
