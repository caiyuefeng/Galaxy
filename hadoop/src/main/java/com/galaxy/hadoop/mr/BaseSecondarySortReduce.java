package com.galaxy.hadoop.mr;

import com.galaxy.base.DataType;
import com.galaxy.hadoop.context.WrappedReduceSecondarySortContext;
import com.galaxy.hadoop.writable.DataTypeKey;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.galaxy.base.ConstantCounter.CODE_204;
import static com.galaxy.base.ConstantCounter.GROUP_200;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 二次排序 Reduce基类
 * @date : 2018/12/11 13:48
 **/
public abstract class BaseSecondarySortReduce extends BasePartitionReduce<DataTypeKey, Text> {

    /**
     * Reduce上下文包装器
     */
    private WrappedReduceSecondarySortContext<DataTypeKey, Text> context;

    /**
     * Reduce聚合的真实值缓存
     */
    private List<Text> values = new ArrayList<>();

    @Override
    protected void setup(Context context) {
        this.context = new WrappedReduceSecondarySortContext<>(context);
        super.setup(this.context);
    }

    @Override
    public void reduce(DataTypeKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        if (super.take(key, values, this.context)) {
            String[] keys = partitioner.decode((key));
            this.values.clear();
            boolean check = false;
            for (Text value : values) {
                if (value == null || StringUtils.isEmpty(value.toString())) {
                    continue;
                }
                // 获取数据的新旧类型
                String dataType = StringUtils.substringAfterLast(value.toString(), "\t");
                // 如果第一个不是新数据则直接退出不处理该部分数据
                if (!check && !DataType.NEW.getValue().equals(dataType)) {
                    context.getCounter(GROUP_200, CODE_204).increment(1);
                    return;
                }
                this.values.add(new Text(StringUtils.substringBeforeLast(value.toString(), "\t")));
                check = true;
            }
            reduce(keys[1], this.values, this.context);
        }
    }
}
