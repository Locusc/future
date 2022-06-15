package cn.locusc.dubbo.api.service;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

@SPI("one")
public interface DubboSpiService {

    String notifyMessage(String message);

    // 根据url获取指定的扩展点
    @Adaptive
    String notifyMessage(URL url);

}
