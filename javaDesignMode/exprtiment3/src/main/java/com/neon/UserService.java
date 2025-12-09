package com.neon;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserService extends Remote {
    User findUserByID(String userID) throws RemoteException;
}
