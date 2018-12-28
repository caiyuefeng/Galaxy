package com.galaxy.sun;

import com.galaxy.sun.hadoop.job.DefaultJob;
import com.galaxy.sun.hadoop.workflow.BreakPointWorkflow;

import java.io.IOException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: Demo主函数入口
 * @date : 2018/12/11 9:22
 **/
public class DemoMain {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        BreakPointWorkflow workflow = new BreakPointWorkflow("DemoWorkflow");
        if ("0".equals(args[0])) {
            DefaultJob job = new DefaultJob();
            job.setJobName("GalaxyStandardPartitionJob");
            workflow.getJobPool().add(job);
        } else if ("1".equals(args[0])) {
            DefaultJob job = new DefaultJob();
            job.setJobName("GalaxyImportPartitionJob");
            workflow.getJobPool().add(job);
        } else if ("2".equals(args[0])) {
            DefaultJob job = new DefaultJob();
            job.setJobName("GalaxyMergePartitionJob");
            workflow.getJobPool().add(job);
        } else {
            DefaultJob job1 = new DefaultJob();
            job1.setJobName("GalaxyStandardPartitionJob");
            workflow.getJobPool().add(job1);
            DefaultJob job2 = new DefaultJob();
            job2.setJobName("GalaxyImportPartitionJob");
            workflow.getJobPool().add(job2);
            DefaultJob job3 = new DefaultJob();
            job3.setJobName("GalaxyMergePartitionJob");
            workflow.getJobPool().add(job3);
        }
        workflow.work();
    }
}
