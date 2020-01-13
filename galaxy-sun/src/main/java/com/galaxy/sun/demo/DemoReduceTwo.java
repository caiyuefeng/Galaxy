package com.galaxy.sun.demo;

import com.galaxy.sun.hadoop.context.WrappedContext;
import com.galaxy.sun.hadoop.mr.BaseSecondarySortReduce;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.galaxy.sun.base.ConstantCounter.*;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2018/12/11 15:46
 **/
public class DemoReduceTwo extends BaseSecondarySortReduce {

    private List<String[]> valueList = new ArrayList<>();

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void setup(WrappedContext wrappedContext) {

    }

    @Override
    public void reduce(String key, Iterable<Text> values, WrappedContext context) throws IOException, InterruptedException {

        valueList.clear();
        for (Text value : values) {
            valueList.add(value.toString().split("\t", -1));
        }
        if (valueList.size() <= 1) {
            context.getCounter(GROUP_200, CODE_203).increment(1);
            return;
        }
        for (int i = 0; i < valueList.size(); i++) {
            String[] first = valueList.get(i);
            for (int j = i + 1; j < valueList.size(); j++) {
                String[] second = valueList.get(j);
                if (first[1].equals(second[1])) {
                    context.getCounter(GROUP_200, CODE_202).increment(1);
                    continue;
                }
                builder.setLength(0);
                if (first[1].compareTo(second[1]) < 0) {
                    builder.append(first[0]).append("\t")
                            .append(first[1]).append("\t")
                            .append(second[0]).append("\t")
                            .append(second[1]).append("\t")
                            .append("1");
                } else {
                    builder.append(second[0]).append("\t")
                            .append(second[1]).append("\t")
                            .append(first[0]).append("\t")
                            .append(first[1]).append("\t")
                            .append("1");
                }
                context.getCounter(GROUP_100,CODE_102).increment(1);
                context.write(builder.toString());
            }
        }
    }
}
