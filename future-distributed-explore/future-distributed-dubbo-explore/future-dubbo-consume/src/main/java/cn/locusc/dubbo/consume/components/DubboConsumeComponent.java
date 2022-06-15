package cn.locusc.dubbo.consume.components;

import cn.locusc.dubbo.api.service.DubboDemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

/**
 * @author Jay
 * random
 * 2022/6/7
 */
@Component
public class DubboConsumeComponent {

    // force:return 1234
    @Reference(loadbalance = "onlyFirst", mock = "fail:return 1234")
    // @Reference
    private DubboDemoService dubboDemoService;

    public String notifyMessage(String message){
        return dubboDemoService.notifyMessage(message);
    }

    public String loanBalance(String message) {
        return dubboDemoService.loadBalance(message);
    }
}
