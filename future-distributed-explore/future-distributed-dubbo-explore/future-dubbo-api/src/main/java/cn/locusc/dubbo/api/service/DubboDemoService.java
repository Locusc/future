package cn.locusc.dubbo.api.service;

public interface DubboDemoService {

    String notifyMessage(String message);

    String loadBalance(String message);

}
