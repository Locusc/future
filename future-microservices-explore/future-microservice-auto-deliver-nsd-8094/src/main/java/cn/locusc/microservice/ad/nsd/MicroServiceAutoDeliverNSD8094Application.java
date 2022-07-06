package cn.locusc.microservice.ad.nsd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroServiceAutoDeliverNSD8094Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceAutoDeliverNSD8094Application.class,args);
    }

}

