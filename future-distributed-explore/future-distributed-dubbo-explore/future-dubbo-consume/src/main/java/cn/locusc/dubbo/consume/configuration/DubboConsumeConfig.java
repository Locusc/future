package cn.locusc.dubbo.consume.configuration;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@EnableDubbo
@ComponentScan(basePackages = "com.lagou.bean")
@PropertySource("classpath:dubbo-consumer.properties")
public class DubboConsumeConfig { }
