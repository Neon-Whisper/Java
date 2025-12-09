package com.neon;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private List<User> users;

    public UserServiceImpl() throws RemoteException {
        super();
        this.users = new ArrayList<>();
        // 初始化至少两个用户
        users.add(new User("u001", "张三"));
        users.add(new User("u002", "李四"));
    }

    @Override
    public User findUserByID(String userID) throws RemoteException {
        System.out.println("RMI客户机请求: " + userID);
        for (User user : users) {
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        throw new RemoteException("The user does not exist!");
    }
}
