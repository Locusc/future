package cn.locusc.microservice.ad.nsf.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level logConf() {
        return Logger.Level.FULL;
    }

}
