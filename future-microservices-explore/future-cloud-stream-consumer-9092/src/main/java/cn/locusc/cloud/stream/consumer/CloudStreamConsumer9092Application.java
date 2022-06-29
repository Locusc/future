package cn.locusc.cloud.stream.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CloudStreamConsumer9091Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudStreamConsumer9091Application.class, args);
    }

}
