package com.galaxy.examples.uranus;

import com.galaxy.uranus.annotation.OptionAnnotation;
import com.galaxy.uranus.annotation.OptionBindType;
import com.galaxy.uranus.annotation.OptionBindTypeEnum;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 22:35 2019/12/30
 *
 */
@OptionBindType(value = OptionBindTypeEnum.VALUE_BIND)
@OptionAnnotation(opt = "uranus", longOpt = "uranus_test",
		bindValue = "2019", valueSeq = '-', hasArgs = true, numOfArgs = 1,
		desc = "时间", groupName = "Test")
public class ValueBindFunc {
}
