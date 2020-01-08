package com.galaxy.uranus;

import com.galaxy.uranus.option.Options;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: 帮助文档构建器
 * 该工具类通过接受定义好的Options参数项，
 * 显示格式:
 * ${HelpInfo_Head} -> 帮助文档头部信息
 * ${HelpInfo_Command} -> ${execute_command} -{option_name} -{option_value} 执行模式或方式
 * ${HelpInfo_Examples} -> 执行样例
 * ${HelpInfo_Option} -> 参数项说明
 * ${HelpInfo_Tail} -> 帮助文档尾部信息
 * 如 GalaxyFramework 运行平台命令
 * galaxy -[start|stop|restart|status] -[Module_Name]
 * com.galaxy.uranus.examples:
 *   galaxy -start sirius
 *   galaxy -stop sirius
 * options:
 *  -start --start
 * @Date : Create in 19:34 2020/1/1
 * @Modified By:
 */
public class HelpInfoFormat {

	public HelpInfoFormat(Options options) {

	}

}
