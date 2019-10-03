package com.galaxy.saturn;

import com.galaxy.boot.annotation.Run;
import com.galaxy.saturn.thread.GalaxyMonitor;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description: Saturn 主程序入口
 * @date : 2018/12/24 9:51
 **/
public class SaturnRun implements Watcher {

    /**
     * 日志句柄
     */
    private static final Logger LOG = LoggerFactory.getLogger(SaturnRun.class);

    @Run
    public void executor() {
        GalaxyMonitor monitor = new GalaxyMonitor();
        monitor.monitor();
        while (true) {
            System.out.println("主线程!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//        if (args.length == 0) {
//            usage();
//            return;
//        }
//        switch (args[0]) {
//            case "0":
//                DistributeExecute execute = new DistributeExecute();
//                execute.execute();
//                break;
//            case "1":
//                break;
//            default:
//                break;
//        }
    }

    private static void usage() {
        System.out.println("================================");
        System.out.println("usage:");
        System.out.println("java -jar GalaxySaturn-1.0.jar 0 执行分布式数据处理");
        System.out.println("java -jar GalaxySaturn-1.0.jar 1 文件上传至Zookeeper");
        System.out.println("================================");
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        LOG.info("Zookeeper 连接成功!");
    }
}
