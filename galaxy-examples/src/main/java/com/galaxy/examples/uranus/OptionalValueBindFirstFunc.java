package com.galaxy.examples.uranus;

import com.galaxy.uranus.annotation.OptionAnnotation;
import com.galaxy.uranus.annotation.OptionBindType;
import com.galaxy.uranus.annotation.OptionBindTypeEnum;
import com.galaxy.uranus.annotation.OptionalArgument;

/**
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 20:20 2020/1/9
 */
@OptionBindType(value = OptionBindTypeEnum.VALUE_BIND)
@OptionAnnotation(opt = "m", longOpt = "mode", bindValue = "a", hasArgs = true, numOfArgs = 1, groupName = "Test")
public class OptionalValueBindFirstFunc {
}
