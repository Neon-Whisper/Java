package com.neon;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class MyDataSource {
    // 饿汉式单例实例
    private static final MyDataSource instance = new MyDataSource();
    // 连接池容器
    private List<MyConnection> connectionPool;
    // 池大小
    private static final int POOL_SIZE = 5;

    // 私有构造，初始化连接池
    private MyDataSource() {
        connectionPool = new ArrayList<>(POOL_SIZE);
        for (int i = 0; i < POOL_SIZE; i++) {
            connectionPool.add(new MyConnection());
        }
    }

    // 获取单例
    public static MyDataSource getInstance() {
        return instance;
    }

    // 从连接池获取连接
    public MyConnection getConnection() {
        if (connectionPool.isEmpty()) {
            System.out.println("连接池为空");
            return null;
        }
        return connectionPool.remove(0);
    }

    // 归还连接到池
    public void returnConnection(MyConnection conn) {
        connectionPool.add(conn);
        System.out.println("连接归还到池，当前池大小：" + connectionPool.size());
    }

    public static class MyConnection {
        private String name;

        public MyConnection(String name) {
            this.name = name;
        }

        public void executeQuery(String sql) {
            System.out.println(name + "执行查询：" + sql);
        }

        public String getName() {
            return name;
        }
    }
}