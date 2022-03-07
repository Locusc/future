package cn.locusc.spring.boot.rjmt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@MapperScan("cn.locusc.spring.boot.rjmt.data.mapper")
@SpringBootApplication
public class SpringbootRjmtLogosApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRjmtLogosApplication.class, args);
    }

}
