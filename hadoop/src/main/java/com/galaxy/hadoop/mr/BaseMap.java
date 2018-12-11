package com.galaxy.hadoop.mr;

import com.galaxy.hadoop.context.WrappedContext;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: Map基类
 * @date : 2018/12/10 14:11
 **/
public abstract class BaseMap<KI, VI, KO, VO> extends Mapper<KI, VI, KO, VO> {

    /**
     * 基类初始化方法接口
     *
     * @param context 基类上下文包装器
     */
    public abstract void setup(WrappedContext context);

    /**
     * Map业务逻辑代码接口
     *
     * @param value   Map读入行
     * @param context 基类包装类型上下文
     * @throws IOException          1
     * @throws InterruptedException 2
     */
    public abstract void map(String value, WrappedContext context) throws IOException, InterruptedException;

    /**
     * 基类Mapper默认操作
     *
     * @param key            Map输入Key
     * @param value          Map输入Value
     * @param context 基类上下文包装器
     * @return 默认操作成功与否标志
     */
    public abstract boolean take(KI key, VI value, WrappedContext context);

}
