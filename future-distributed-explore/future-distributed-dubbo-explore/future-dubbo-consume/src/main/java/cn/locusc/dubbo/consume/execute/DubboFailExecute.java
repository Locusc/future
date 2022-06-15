package cn.locusc.dubbo.consume.execute;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;

public class DubboFailExecute {

    public static void main(String[] args) {
        RegistryFactory registryFactory =
                ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();

        Registry registry = registryFactory.getRegistry(URL.valueOf("zookeeper://127.0.0.1:2181"));

        String url = "override://0.0.0.0/cn.locusc.dubbo.api.service.DubboDemoService" +
                "?category=configurators&dynamic=false&application=future-dubbo-consume";

        registry.register(URL.valueOf(url));
    }

}
