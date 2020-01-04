package com.galaxy.uranus.examples;

import com.galaxy.uranus.annotation.OptionAnnotation;
import com.galaxy.uranus.annotation.OptionBindType;
import com.galaxy.uranus.annotation.OptionBindTypeEnum;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:35 2019/12/30
 * @Modified By:
 */
@OptionBindType(value = OptionBindTypeEnum.VALUE_BIND)
@OptionAnnotation(opt = "uranus", longOpt = "uranus_test", isRequired = true,
		value = "2019", valueSeq = '-', hasArgs = true, numOfArgs = 1,
		desc = "时间", groupName = "Test")
public class ValueBindFunc {
}
