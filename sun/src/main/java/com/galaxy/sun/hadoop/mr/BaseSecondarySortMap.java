package com.galaxy.sun.hadoop.mr;

import com.galaxy.sun.base.DataType;
import com.galaxy.sun.base.FileNameType;
import com.galaxy.sun.hadoop.context.WrappedMapSecondarySortContext;
import com.galaxy.sun.hadoop.writable.DataTypeKey;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 二次排序 Map基类
 * @date : 2018/12/11 13:48
 **/
public abstract class BaseSecondarySortMap<KI, VI> extends BasePartitionMap<KI, VI, DataTypeKey> {

    private WrappedMapSecondarySortContext<KI, VI> context;

    private String dataType = DataType.OLD.getValue();

    @Override
    public void setup(Context context) {
        this.context = new WrappedMapSecondarySortContext<>(context);
        super.setup(this.context);
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
        if (super.take(key, value, this.context)) {
            // 调用业务逻辑代码
            map(super.realValue, this.context);
        }
    }
}
