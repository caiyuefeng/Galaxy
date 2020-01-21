package com.galaxy.examples.uranus;

import com.galaxy.uranus.annotation.OptionAnnotation;
import com.galaxy.uranus.annotation.OptionBindType;
import com.galaxy.uranus.annotation.OptionBindTypeEnum;
import com.galaxy.uranus.annotation.OptionalArgument;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 21:32 2020/1/8
 *
 */
@OptionalArgument({"2017", "2018"})
@OptionBindType(value = OptionBindTypeEnum.VALUE_BIND)
@OptionAnnotation(opt = "opt", longOpt = "optional",
		bindValue = "2017", valueSeq = '-', hasArgs = true, numOfArgs = 2,
		desc = "时间", groupName = "Test")
public class OptionalValueBindFunc {
}
