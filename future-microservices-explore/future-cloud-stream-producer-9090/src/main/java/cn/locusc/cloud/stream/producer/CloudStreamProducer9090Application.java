package cn.locusc.cloud.stream.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CloudStreamProducer9090Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudStreamProducer9090Application.class, args);
    }

}
