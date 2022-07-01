package cn.locusc.microservice.oauth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan("cn.locusc.microservice.common.domain")
public class MicroServiceOauth9999Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceOauth9999Application.class, args);
    }

}
