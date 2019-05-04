package com.galaxy.asteroid.thread;

import java.io.IOException;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:11 2019/5/4
 * @Modified By:
 */
public class RpcTest {

    public static void main(String[] args) throws IOException {
        GalaxyRpcServer server = new GalaxyRpcServer(8088);
        server.register(Person.class.getName(),Student.class);
        server.start();
        new Thread(server).start();
        GalaxyRpcClient client = new GalaxyRpcClient(8088);
        Person p = (Person) client.getServer(Person.class);
        p.speak();
        p.speak();
        p.speak();
        server.stop();
        System.out.println(111);
    }
}
