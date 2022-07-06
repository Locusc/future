package cn.locusc.microservice.ad.nsf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MicroServiceAutoDeliverNSF8093Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceAutoDeliverNSF8093Application.class, args);
    }

}
