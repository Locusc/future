package cn.locusc.microservice.resume.nacos;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EntityScan("cn.locusc.microservice.common.domain")
@EnableDiscoveryClient
public class MicroServiceResumeNacos8082Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceResumeNacos8082Application.class,args);
    }

}
