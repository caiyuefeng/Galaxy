package com.galaxy.hadoop.mr;

import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/10 14:11
 **/
public abstract class BaseMap<KI, VI, KO, VO> extends Mapper<KI, VI, KO, VO> {

}
