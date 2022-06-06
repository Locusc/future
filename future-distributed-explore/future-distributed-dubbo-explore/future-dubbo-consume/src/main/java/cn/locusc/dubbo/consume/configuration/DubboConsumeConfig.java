package cn.locusc.dubbo.consume.configuration;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@EnableDubbo
@ComponentScan(basePackages = "cn.locusc.dubbo.consume.components")
@PropertySource("classpath:dubbo-consume.properties")
public class DubboConsumeConfig { }
