package cn.locusc.seata.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,
        scanBasePackages = "cn.locusc.seata")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"cn.locusc.seata.business.feign"})
@EnableTransactionManagement
public class TransactionSeataBusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionSeataBusinessApplication.class, args);
    }

}
