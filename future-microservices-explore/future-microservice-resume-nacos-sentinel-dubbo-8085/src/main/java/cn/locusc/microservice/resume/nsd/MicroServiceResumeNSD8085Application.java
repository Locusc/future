package cn.locusc.microservice.resume.nsd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EntityScan("cn.locusc.microservice.common.domain")
@EnableDiscoveryClient
public class MicroServiceResumeNSD8085Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceResumeNSD8085Application.class,args);
    }

}
