package com.galaxy.sun.hadoop.fileformat;

import com.galaxy.earth.date.DateUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 默认输出文件名
 * 以 指定的字段前缀 + 日期 + 同名文件数 + nb
 * @date : 2018/12/10 14:21
 **/
public class DefaultFileOutputFormat extends BaseFileOutputFormat {

    /**
     * 输入文件数
     */
    private long cnt = 0L;

    /**
     * 当前文件索引数
     */
    private long index = 0L;

    @Override
    public String getCustomFileName(String para) {
        String prefix = StringUtils.isEmpty(para) ? "GALAXY" : "GALAXY_PS" + para.toUpperCase() + "PE";
        cnt++;
        if (cnt > maxLineSize) {
            cnt = 1;
            index++;
        }
        return prefix + "_" + DateUtils.getDate() + "_" + index + ".nb";
    }

    @Override
    public void closeAll() {
    }
}
