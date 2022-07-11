package cn.locusc.seata.storage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,
        scanBasePackages = "cn.locusc.seata")
@EnableDiscoveryClient
@MapperScan(basePackages = {"cn.locusc.seata.storage.mapper"}) // mybatis包扫描
public class TransactionSeataStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionSeataStorageApplication.class, args);
    }

}
