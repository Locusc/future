package cn.locusc.dubbo.producer.service.impl;

import cn.locusc.dubbo.api.service.DubboSpiService;
import org.apache.dubbo.common.URL;

public class DubboSpiOneServiceImpl implements DubboSpiService {

    @Override
    public String notifyMessage(String message) {
        return "one: " + message;
    }

    @Override
    public String notifyMessage(URL url) {
        return "one url: " + url;
    }

}
