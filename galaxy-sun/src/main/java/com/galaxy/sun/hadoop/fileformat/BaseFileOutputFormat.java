package com.galaxy.sun.hadoop.fileformat;

import com.galaxy.sun.base.ConstantChar;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  输出文件名基类
 * @date 2018/12/10 14:21
 **/
public abstract class BaseFileOutputFormat<K, V> extends FileOutputFormat<K, V> {

    /**
     * 输出文件句柄
     */
    private Map<Path, RecordWriter<K, V>> recordWriterMap = new HashMap<>();

    /**
     * 同一文件中最大文件条数
     */
    long maxLineSize = 1000000L;

    @Override
    public RecordWriter<K, V> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException {
        Path basePath = getCurrentOutputPath(taskAttemptContext);
        FileSystem fs = FileSystem.get(taskAttemptContext.getConfiguration());
        DefaultRecordWriter<K, V> writer = new DefaultRecordWriter<>(fs, basePath);
        if (recordWriterMap.containsKey(writer.getOutputPath())) {
            return recordWriterMap.get(writer.getOutputPath());
        }
        recordWriterMap.put(writer.getOutputPath(), writer);
        return writer;
    }

    /**
     * 自定义输出文件名
     *
     * @param para 参数
     * @return 文件名
     */
    public abstract String getCustomFileName(String para);

    /**
     * 关闭缓存
     */
    public abstract void closeAll();

    private class DefaultRecordWriter<k, v> extends RecordWriter<k, v> {

        /**
         * 文件系统
         */
        private FileSystem fs;

        /**
         * 输入根路径
         */
        private Path basePath;

        /**
         * 当前输出路径
         */
        private Path outputPath;

        /**
         * 输出文件流
         */
        private Map<Path, FSDataOutputStream> outputStreamMap = new HashMap<>();

        DefaultRecordWriter(FileSystem fs, Path basePath) {
            this.fs = fs;
            this.basePath = basePath;
        }

        @Override
        public void write(k k, v v) throws IOException {
            outputPath = new Path(basePath, getCustomFileName(k.toString()));
            if (outputStreamMap.containsKey(outputPath)) {
                write(outputStreamMap.get(outputPath), v);
                return;
            }
            FSDataOutputStream outputStream = fs.create(outputPath);
            write(outputStream, v);
            outputStreamMap.put(new Path(outputPath.toString()), outputStream);
        }

        private void write(FSDataOutputStream outputStream, v value) throws IOException {
            if (value == null || value instanceof NullWritable) {
                outputStream.write(ConstantChar.NEW_LINE);
                return;
            }
            if (value instanceof Text) {
                Text val = (Text) value;
                outputStream.write(val.toString().getBytes(ConstantChar.UTF8), 0, val.getLength());
            } else {
                outputStream.write(value.toString().getBytes(ConstantChar.UTF8));
            }
            outputStream.write(ConstantChar.NEW_LINE);
        }

        @Override
        public void close(TaskAttemptContext taskAttemptContext) throws IOException {
            for (FSDataOutputStream outputStream : outputStreamMap.values()) {
                outputStream.close();
            }
            outputStreamMap.clear();
            recordWriterMap.clear();
            closeAll();
        }

        Path getOutputPath() {
            return outputPath;
        }
    }

    private Path getCurrentOutputPath(TaskAttemptContext context) throws IOException {
        OutputCommitter committer = super.getOutputCommitter(context);
        if (committer instanceof FileOutputCommitter) {
            return ((FileOutputCommitter) committer).getWorkPath();
        }
        return getOutputPath(context);
    }
}
