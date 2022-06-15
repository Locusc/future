package cn.locusc.dubbo.producer.execute;

import cn.locusc.dubbo.api.service.DubboSpiService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

import java.util.Set;

public class DubboSpiExecute {

    public static void main(String[] args) {
        spiAdaptive();
    }

    private static void spiNormal() {
        ExtensionLoader<DubboSpiService> extensionLoader =
                ExtensionLoader.getExtensionLoader(DubboSpiService.class);
        // 获取所有支持的扩展
        Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        supportedExtensions.forEach(f -> {
            String message = extensionLoader.getExtension(f).notifyMessage("extensionLoader");
            System.out.println(message);
        });
    }

    private static void spiAdaptive() {
        URL url = URL.valueOf("prod://localhost/dubbo?dubbo.spi.service=one");
        DubboSpiService adaptiveExtension = ExtensionLoader.getExtensionLoader(DubboSpiService.class)
                .getAdaptiveExtension();

        String notifyMessage = adaptiveExtension.notifyMessage(url);
        System.out.println(notifyMessage);
    }
    
}
