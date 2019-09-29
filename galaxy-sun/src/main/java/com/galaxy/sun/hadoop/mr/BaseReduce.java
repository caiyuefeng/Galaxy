package com.galaxy.sun.hadoop.mr;

import com.galaxy.sun.hadoop.context.WrappedContext;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: Reduce基类
 * @date : 2018/12/10 15:17
 **/
public abstract class BaseReduce<KI, VI, KO, VO> extends Reducer<KI, VI, KO, VO> {

    /**
     * 基类任务初始化操作
     *
     * @param wrappedContext 基类上下文包装器
     */
    public abstract void setup(WrappedContext wrappedContext);

    /**
     * Reduce业务逻辑代码
     *
     * @param key     K
     * @param values  V
     * @param context 上下文
     * @throws IOException          1
     * @throws InterruptedException 2
     */
    public abstract void reduce(String key, Iterable<VI> values, WrappedContext context) throws IOException, InterruptedException;

    /**
     * Reduce默认操作
     *
     * @param key     Reduce用于聚合的键
     * @param values  Reduce聚合的值集合
     * @param context 基类包装类型上下文
     * @return 默认操作成功与否标志
     */
    public abstract boolean take(KI key, Iterable<VI> values, WrappedContext context);
}
