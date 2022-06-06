package cn.locusc.dubbo.producer.configuration;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "cn.locusc.dubbo.producer.service.impl")
@PropertySource("classpath:dubbo-producer.properties")
public class DubboProducerConfig {

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig  registryConfig  = new RegistryConfig();
        registryConfig.setAddress("zookeeper://127.0.0.1:2181?timeout=10000");
        registryConfig.setTimeout(10000);
        return registryConfig;
    }

}
