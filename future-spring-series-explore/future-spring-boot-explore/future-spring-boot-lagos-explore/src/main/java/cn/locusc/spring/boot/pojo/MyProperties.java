package cn.locusc.spring.boot.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "test")
// 配置自定义配置文件的名称及位置
@PropertySource("classpath:test.properties")
public class MyProperties {

    private int id;
    private String name;

}
