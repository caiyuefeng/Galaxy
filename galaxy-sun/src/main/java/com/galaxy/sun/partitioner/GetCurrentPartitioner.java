package com.galaxy.sun.partitioner;

import com.galaxy.sun.base.FileNameType;
import org.apache.commons.lang.StringUtils;

import static com.galaxy.sun.base.ConstantChar.UNDERLINE;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2018/12/11 15:38
 **/
public class GetCurrentPartitioner implements DataPartitioner {
    @Override
    public String encode(Object o) {
        String name = StringUtils.substringAfterLast(o.toString(), "/");
        return format(name.substring(name.indexOf("PS") + 2, name.indexOf("PE")));
    }

    /**
     * 去除分区中带有的IMPORT 和TOTAL子分区
     *
     * @param part 分区信息
     * @return 格式化后分区
     */
    private String format(String part) {
        if (part.contains(FileNameType.TOTAL.getValue()) || part.contains(FileNameType.IMPORT.getValue())) {
            return StringUtils.substringBeforeLast(part, UNDERLINE);
        }
        return part;
    }

    private final String[] values = new String[2];

    @Override
    public String[] decode(Object o) {
        values[0] = StringUtils.substringAfterLast(o.toString(), "\t");
        values[1] = StringUtils.substringBeforeLast(o.toString(), "\t");
        return values;
    }
}
