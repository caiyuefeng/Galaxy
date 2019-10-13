package com.galaxy.saturn;

import com.galaxy.boot.annotation.Run;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: Saturn 主程序入口
 * @date : 2018/12/24 9:51
 **/
public class SaturnRun {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(SaturnRun.class);

    @Run
    public void executor(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }

        switch (args[0]) {
            case "0":
                SaturnClient client = new SaturnClient();
                client.run();
                break;
            case "1":
            default:
                break;
        }
    }

    private static void usage() {
        System.out.println("================================");
        System.out.println("usage:");
        System.out.println("java -jar galaxy-saturn-1.0.jar 0 执行分布式数据处理");
        System.out.println("java -jar galaxy-saturn-1.0.jar 1 文件上传至Zookeeper");
        System.out.println("================================");
    }
}
