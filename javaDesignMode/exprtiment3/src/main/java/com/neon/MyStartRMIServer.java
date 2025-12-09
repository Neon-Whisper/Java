
package com.neon;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class MyStartRMIServer {
    public static void main(String[] args) {
        int port = 9911;
        String ip = "localhost";
        String serviceName = "userService";

        try {
            LocateRegistry.createRegistry(port);
            UserServiceImpl service = new UserServiceImpl();
            Naming.rebind("rmi://" + ip + ":" + port + "/" + serviceName, service);
            System.out.println("RMI 服务器已启动，监听端口：" + port);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
