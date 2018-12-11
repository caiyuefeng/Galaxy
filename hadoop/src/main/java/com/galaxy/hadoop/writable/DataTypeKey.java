package com.galaxy.hadoop.writable;

import com.galaxy.base.DataType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 新旧数据的二次排序键
 * @date : 2018/12/11 13:49
 **/
public class DataTypeKey implements WritableComparable<DataTypeKey> {

    /**
     * 真实键值
     */
    private Text value = new Text();

    /**
     * 排序键值
     */
    private Text sort = new Text();


    public Text getValue() {
        return value;
    }

    public void setValue(Text value) {
        this.value = value;
    }

    public Text getSort() {
        return sort;
    }

    public void setSort(Text sort) {
        this.sort = sort;
    }

    @Override
    public int compareTo(DataTypeKey that) {
        if (that == null) {
            return 1;
        }
        int result = this.value.toString().compareTo(that.value.toString());
        if (result != 0) {
            return result;
        }
        if (DataType.NEW.getValue().equals(this.sort.toString())) {
            return -1;
        }
        if (DataType.OLD.getValue().equals(that.sort.toString())) {
            return 1;
        }
        return 0;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        value.write(dataOutput);
        sort.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        value.readFields(dataInput);
        sort.readFields(dataInput);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataTypeKey that = (DataTypeKey) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }
}
