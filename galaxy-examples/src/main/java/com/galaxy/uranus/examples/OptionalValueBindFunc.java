package com.galaxy.uranus.examples;

import com.galaxy.uranus.annotation.OptionAnnotation;
import com.galaxy.uranus.annotation.OptionBindType;
import com.galaxy.uranus.annotation.OptionBindTypeEnum;
import com.galaxy.uranus.annotation.OptionalArgument;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:32 2020/1/8
 * @Modified By:
 */
@OptionalArgument({"2017", "2018"})
@OptionBindType(value = OptionBindTypeEnum.VALUE_BIND)
@OptionAnnotation(opt = "opt", longOpt = "optional",
		value = "2017", valueSeq = '-', hasArgs = true, numOfArgs = 2,
		desc = "时间", groupName = "Test")
public class OptionalValueBindFunc {
}
