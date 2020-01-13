package com.galaxy.sun.hadoop.mr;

import com.galaxy.sun.base.DataType;
import com.galaxy.sun.base.FileNameType;
import com.galaxy.sun.compress.DataCompress;
import com.galaxy.sun.hadoop.context.WrappedContext;
import com.galaxy.sun.hadoop.context.WrappedMapSecondarySortContext;
import com.galaxy.sun.hadoop.writable.DataTypeKey;
import com.galaxy.sun.partitioner.DataPartitioner;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.galaxy.sun.base.ConstantCounter.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  二次排序 Map基类
 * @date 2018/12/11 13:48
 **/
public abstract class BaseSecondarySortMap<KI, VI> extends BaseMap<KI, VI, DataTypeKey,Text> {

    private WrappedMapSecondarySortContext<KI, VI> context;

    /**
     * 当前数据类型
     */
    private String dataType = DataType.OLD.getValue();

    /**
     * 分区器
     */
    public DataPartitioner<String> partitioner;

    /**
     * 压缩器
     */
    protected DataCompress compress;

    /**
     * 真实输入值
     */
    protected String realValue;

    @Override
    public final void setup(Context context) {
        this.context = new WrappedMapSecondarySortContext<>(context);
        String path;
        try {
            path = getCurrentFileName(context);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        // 获取当前文件的新旧类型
        if (path.contains(FileNameType.IMPORT.getValue())) {
            dataType = DataType.OLD.getValue();
        } else if (path.contains(FileNameType.TOTAL.getValue())) {
            dataType = DataType.NEW.getValue();
        }
        this.context.setDefaultSortSeed(new Text(dataType));
    }

    @Override
    protected void map(KI key, VI value, Context context) throws IOException, InterruptedException {
        if (take(key, value, this.context)) {
            // 调用业务逻辑代码
            map(realValue, this.context);
        }
    }

    @Override
    public boolean take(KI key, VI value, WrappedContext context) {
        // 统计总输入量
        context.getCounter(GROUP_100, CODE_101).increment(1);
        if (partitioner == null || compress == null) {
            context.getCounter(GROUP_300, CODE_301).increment(1);
            return false;
        }
        //解压缩
        realValue = compress.decompress(value.toString());
        // 设置分区
        context.setDefaultPart(partitioner.encode(realValue));
        return true;
    }
}
