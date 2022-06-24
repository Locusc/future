package cn.locusc.cloud.hystrix.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableTurbine// 开启Turbine聚合功能
@EnableDiscoveryClient
@SpringBootApplication
public class CloudHystrixTurbine9001Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudHystrixTurbine9001Application.class, args);
    }

}
