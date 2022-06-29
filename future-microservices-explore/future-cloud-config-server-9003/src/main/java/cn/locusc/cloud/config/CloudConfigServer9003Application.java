package cn.locusc.cloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;


@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer  // 开启配置中心功能
public class CloudConfigServer9003Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudConfigServer9003Application.class,args);
    }

}
