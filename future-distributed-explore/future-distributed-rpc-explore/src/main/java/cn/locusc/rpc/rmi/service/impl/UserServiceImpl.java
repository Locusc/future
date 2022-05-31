package cn.locusc.rpc.rmi.service.impl;

import cn.locusc.rpc.rmi.domain.User;
import cn.locusc.rpc.rmi.service.IUserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl extends UnicastRemoteObject implements IUserService {

    Map<Object, User> userMap = new HashMap<Object, User>();

    public UserServiceImpl() throws RemoteException {
        super();
        User user1 = new User();
        user1.setId(1);
        user1.setName("Jay");
        User user2 = new User();
        user2.setId(2);
        user2.setName("Locusc");
        userMap.put(user1.getId(), user1);
        userMap.put(user2.getId(), user2);
    }

    public User getByUserId(int id) throws RemoteException {
        return userMap.get(id);
    }

}
