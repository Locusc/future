package cn.locusc.microservice.resume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EntityScan("cn.locusc.microservice.common.domain")
// @EnableEurekaClient  // 开启Eureka Client(Eureka独有)
@EnableDiscoveryClient // 开启注册中心客户端(通用型注解, 比如注册到Eureka, Nacos等)
// 说明: 从SpringCloud的Edgware版本开始, 不加注解也可以进行服务发现
@SpringBootApplication
public class MicroServiceResume8080Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceResume8080Application.class, args);
    }

}
