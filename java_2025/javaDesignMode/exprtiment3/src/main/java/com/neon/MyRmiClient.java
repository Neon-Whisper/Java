package com.neon;

import java.rmi.Naming;

public class MyRmiClient {
    public static void main(String[] args) {
        int port = 9911;
        String ip = "localhost";
        String serviceName = "userService";

        try {
            UserService stub = (UserService) Naming.lookup("rmi://" + ip + ":" + port + "/" + serviceName);
            //测试存在的用户
//            User user = stub.findUserByID("u001");
//            System.out.println("查询结果：" + user);

//            // 测试不存在的用户
            User user = stub.findUserByID("u999");
            System.out.println("查询结果：" + user);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
