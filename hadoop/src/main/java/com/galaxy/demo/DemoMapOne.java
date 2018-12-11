package com.galaxy.demo;

import com.galaxy.hadoop.context.WrappedContext;
import com.galaxy.hadoop.mr.BasePartitionMap;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static com.galaxy.base.ConstantConfItem.MAP_REDUCE_PROPERTIES;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2018/12/11 15:45
 **/
public class DemoMapOne extends BasePartitionMap<LongWritable, Text, Text> {

    private Gson gson = new Gson();

    private Map<Integer, String> dict = new TreeMap<>();

    private final StringBuilder builder = new StringBuilder();

    @Override
    protected void setup(Context context) {
        super.setup(context);
        String bufferValue = context.getConfiguration().get(MAP_REDUCE_PROPERTIES);
        Map<String, String> buffer = gson.fromJson(bufferValue, Map.class);
        for (Map.Entry<String, String> entry : buffer.entrySet()) {
            dict.put(Integer.valueOf(entry.getValue()), entry.getKey());
        }
    }

    @Override
    public void map(String value, WrappedContext context) throws IOException, InterruptedException {
        Map<String, String> buffer = gson.fromJson(value, Map.class);
        builder.setLength(0);
        for (Map.Entry<Integer, String> entry : dict.entrySet()) {
            String val = buffer.get(entry.getValue());
            if (StringUtils.isEmpty(val)) {
                builder.append("\t");
                continue;
            }
            builder.append(val).append("\t");
        }
        context.write(StringUtils.substringBeforeLast(builder.toString(), "\t"));
    }
}
