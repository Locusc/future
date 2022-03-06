package cn.locusc.spring.boot.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "person")
public class Person {

    private Integer id;
    private String name;
    private String age;
    private List hobby;
    private String[] family;
    private Map map;
    private Pet pet;

}
