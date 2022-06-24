package cn.locusc.microservice.auto.deliver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class MicroServiceAutoDeliverFeign8092Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceAutoDeliverFeign8092Application.class, args);
    }

}
