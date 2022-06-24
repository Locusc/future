package cn.locusc.microservice.auto.deliver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableCircuitBreaker
@EnableDiscoveryClient // 开启熔断器功能
// @EnableHystrix // 开启Hystrix功能
@SpringBootApplication
//@SpringCloudApplication
//@SpringBootApplication + @EnableDiscoveryClient + @EnableCircuitBreaker
public class MicroServiceAutoDeliver8090Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceAutoDeliver8090Application.class, args);
    }

}
