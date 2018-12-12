package com.galaxy.sun.hadoop.workflow;

import com.galaxy.sun.hadoop.job.DefaultJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.galaxy.sun.base.ConstantPath.BASE_PATH;
import static com.galaxy.sun.base.ConstantPath.RUNNING_STATE;
import static com.galaxy.sun.base.ConstantPath.SUCCESS_STATE;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: 断点工作流任务
 * @date : 2018/12/10 15:17
 **/
public class BreakPointWorkflow {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(BreakPointWorkflow.class);

    /**
     * 任务池
     */
    private List<DefaultJob> jobPool = new ArrayList<>();

    /**
     * 任务公用配置信息
     */
    private Configuration conf;

    /**
     * 文件系统
     */
    private FileSystem fs;

    /**
     * 工作流工作空间
     */
    private Path rootWorkPath = new Path(BASE_PATH, "workflow");

    /**
     * 工作流名称
     */
    private String workflowName;

    public BreakPointWorkflow(String workflowName) throws IOException {
        conf = new Configuration();
        conf.addResource(new Path("./conf/GalaxyCommonConf.xml"));
        fs = FileSystem.get(conf);
        this.workflowName = workflowName;
    }

    /**
     * 执行工作流
     *
     * @throws IOException            1
     * @throws ClassNotFoundException 2
     * @throws InterruptedException   3
     */
    public void work() throws IOException, ClassNotFoundException, InterruptedException {
        Path workflowWorkPath = new Path(rootWorkPath, workflowName);
        Path workflowStatusPath = new Path(workflowWorkPath, "status");
        // 检查当前工作流是否存在断点任务
        boolean breakpoint = false;
        if (fs.exists(workflowStatusPath) && !fs.exists(new Path(workflowStatusPath, SUCCESS_STATE))) {
            LOG.info("存在断点任务!本次工作流将从断点任务继续执行...");
            breakpoint = true;
        }
        // 设置工作流状态位:正在运行
        fs.delete(workflowStatusPath, true);
        fs.mkdirs(new Path(workflowStatusPath, RUNNING_STATE));
        // 循环遍历运行工作流中的任务
        for (DefaultJob job : jobPool) {
            // 检查断点任务
            if (breakpoint) {
                Path jobWorkPath = new Path(workflowWorkPath, "task/" + job.getJobName());
                if (fs.exists(new Path(jobWorkPath, SUCCESS_STATE))) {
                    LOG.info("任务:[" + job.getJobName() + "]不是断点任务!本次不运行!");
                    continue;
                }
                LOG.info("任务:[" + job.getJobName() + "]是断点任务!本次将继续执行...");
            }
            // 运行当前子任务
            if (!runTask(job, workflowWorkPath)) {
                LOG.error("任务:[" + job.getJobName() + "] 发送异常!工作流将停止运行!");
                return;
            }
            breakpoint = false;
        }
        LOG.info("工作流:[" + workflowName + "] 运行完成!");
        // 设置工作流状态:运行成功
        fs.delete(workflowStatusPath, true);
        fs.mkdirs(new Path(workflowStatusPath, SUCCESS_STATE));
    }

    /**
     * 运行工作流子任务
     *
     * @param job          待运行子任务
     * @param rootWorkPath 当前工作流工作空间
     * @return 运行成功与否标志
     * @throws IOException            1
     * @throws InterruptedException   2
     * @throws ClassNotFoundException 3
     */
    private boolean runTask(DefaultJob job, Path rootWorkPath) throws IOException, InterruptedException, ClassNotFoundException {
        Path jobWorkPath = new Path(rootWorkPath, "task/" + job.getJobName());
        fs.delete(jobWorkPath, true);
        fs.mkdirs(new Path(jobWorkPath, "running"));
        Configuration conf = new Configuration(this.conf);
        if (!addResource(conf, job.getJobName())) {
            LOG.error("任务:[" + job.getJobName() + "] 加载任务配置文件失败!任务停止运行!");
            return false;
        }
        if (job.run(conf)) {
            fs.delete(jobWorkPath, true);
            fs.mkdirs(new Path(jobWorkPath, "success"));
            return true;
        }
        return false;
    }

    /**
     * 加载任务配置文件
     *
     * @param conf 任务配置信息
     * @param name 配置文件名称
     * @return 加载成功与否标志
     */
    private boolean addResource(Configuration conf, String name) {
        File resource = new File("./conf/workflow/" + name + ".xml");
        if (!resource.exists()) {
            LOG.error("配置文件不存在!配置文件路径:[./conf/workflow/" + name + ".xml");
            return false;
        }
        LOG.info("成功加载任务配置文件!配置文件路径:[./conf/workflow/" + name + ".xml");
        conf.addResource(new Path(resource.getAbsolutePath()));
        return true;
    }

    public List<DefaultJob> getJobPool() {
        return jobPool;
    }
}
