package cn.locusc.cloud.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import zipkin2.server.internal.EnableZipkinServer;

import javax.sql.DataSource;

@EnableZipkinServer
@SpringBootApplication
public class CloudZipkinServer9411Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudZipkinServer9411Application.class, args);
    }

    // 注入事务控制器
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
