package com.galaxy.asteroid.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 20:49 2019/5/4
 * @Modified By:
 */
public class GalaxyRpcServer implements Runnable {


    private ServerSocket server;

    private Map<String, Class<?>> service;

    private volatile boolean run = false;

    public GalaxyRpcServer(int port) {
        // 初始化套接字服务端
        try {
            this.server = new ServerSocket();
            this.server.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        service = new HashMap<>();
    }

    public void register(String name, Class<?> classType) {
        service.put(name, classType);
    }

    @Override
    public void run() {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            System.out.println(1222222);
            socket = server.accept();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println(1233333333);
            while (run) {
                System.out.println(1212);
                String className = in.readUTF();
                String methodName = in.readUTF();
                Class[] parameterType = (Class[]) in.readObject();
                Object[] parameterValues = (Object[]) in.readObject();
                if (!service.containsKey(className)) {
                    out.writeObject(new RuntimeException("服务中心不含有该服务:" + className));
                    continue;
                }
                Class<?> instanceType = service.get(className);
                Method method = instanceType.getMethod(methodName, parameterType);
                Object value = method.invoke(instanceType.newInstance(), parameterValues);
                out.writeObject(value);
            }
        } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            if (out != null) {
                try {
                    out.writeObject(e);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.shutdownOutput();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void start() {
        run = true;
    }

    public void stop() {
        run = false;
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
