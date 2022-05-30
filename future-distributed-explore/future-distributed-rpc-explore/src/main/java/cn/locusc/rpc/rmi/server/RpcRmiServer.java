package cn.locusc.rpc.rmi.server;

import cn.locusc.rpc.rmi.service.impl.UserServiceImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RpcRmiServer {

    public static void main(String[] args) throws RemoteException {
        // 1.注册Registry实例. 绑定端口
        Registry registry = LocateRegistry.createRegistry(9998);
        // 2.创建远程对象
        UserServiceImpl userService = new UserServiceImpl();
        // 3.将远程对象注册到RMI服务器上即(服务端注册表上)
        registry.rebind("userService", userService);
        System.out.println("rmi server start");
    }

}
