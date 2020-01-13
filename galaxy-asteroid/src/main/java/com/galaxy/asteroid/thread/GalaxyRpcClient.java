package com.galaxy.asteroid.thread;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 20:49 2019/5/4
 *
 */
public class GalaxyRpcClient {

    private Socket socket;

    private class RpcHandle implements InvocationHandler {
        private ObjectOutputStream out;
        private ObjectInputStream in;

        private RpcHandle(OutputStream out, InputStream in) {
            try {
                this.out = new ObjectOutputStream(out);
                this.in = new ObjectInputStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            out.writeUTF(proxy.getClass().getInterfaces()[0].getName());
            out.writeUTF(method.getName());
            out.writeObject(method.getGenericParameterTypes());
            out.writeObject(args);
            out.flush();
            Object o = in.readObject();
            if (o instanceof Throwable) {
                throw (Throwable) o;
            }
            return o;
        }
    }

    public GalaxyRpcClient(int port) {
        this.socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Object getServer(Class<?> service) throws IOException {
        RpcHandle handle = new RpcHandle(socket.getOutputStream(), socket.getInputStream());
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{service}, handle);
    }
}
