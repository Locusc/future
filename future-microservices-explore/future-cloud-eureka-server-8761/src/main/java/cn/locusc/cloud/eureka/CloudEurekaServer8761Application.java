package cn.locusc.cloud.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer// 声明当前项目为Eureka服务
@SpringBootApplication
public class CloudEurekaServer8761Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudEurekaServer8761Application.class, args);
    }

}
