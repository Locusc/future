package cn.locusc.rpc.rmi.service;

import cn.locusc.rpc.rmi.domain.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUserService extends Remote {

    User getByUserId(int id) throws RemoteException;

}
