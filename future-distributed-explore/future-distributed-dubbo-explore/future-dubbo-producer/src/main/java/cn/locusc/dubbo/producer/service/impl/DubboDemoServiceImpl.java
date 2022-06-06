package cn.locusc.dubbo.producer.service.impl;

import cn.locusc.dubbo.api.service.DubboDemoService;
import org.apache.dubbo.config.annotation.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DubboDemoServiceImpl implements DubboDemoService {

    @Override
    public String notifyMessage(String message) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.format("message is: %s", message);
    }

}
