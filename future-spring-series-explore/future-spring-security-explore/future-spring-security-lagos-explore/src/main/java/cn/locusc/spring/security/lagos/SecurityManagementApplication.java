package cn.locusc.spring.security.lagos;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"cn.locusc.spring.security.lagos.mapper"}) // mybatis包扫描
public class SecurityManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityManagementApplication.class, args);
    }

}
