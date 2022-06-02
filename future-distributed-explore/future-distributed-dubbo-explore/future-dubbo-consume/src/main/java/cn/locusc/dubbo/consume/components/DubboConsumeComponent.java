package cn.locusc.dubbo.consume.components;

import cn.locusc.dubbo.api.service.DubboDemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class DubboConsumeComponent {

    @Reference
    private DubboDemoService dubboDemoService;

    public String notifyMessage(String message){
        return dubboDemoService.notifyMessage(message);
    }
}
