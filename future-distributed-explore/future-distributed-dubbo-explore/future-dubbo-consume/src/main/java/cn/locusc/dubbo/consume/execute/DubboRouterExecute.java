package cn.locusc.dubbo.consume.execute;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;

/**
 * @author Jay
 * 向注册中心增加一个条件路由规则
 * 2022/6/8
 */
public class DubboRouterExecute {

    public static void main(String[] args) {
        RegistryFactory adaptiveExtension = ExtensionLoader.getExtensionLoader(RegistryFactory.class)
                .getAdaptiveExtension();

        // 通过dubbo扩展机制获取zookeeper配置中心
        Registry registry = adaptiveExtension.getRegistry(URL.valueOf("zookeeper://127.0.0.1:2181"));

        String url = "condition://0.0.0.0/cn.locusc.dubbo.api.service.DubboDemoService" +
                "?category=routers&force=true&dynamic=true&rule=";

        registry.register(URL.valueOf(String.format("%s%s", url, URL.encode("=> host != 192.168.20.1"))));
    }

}
