package cn.locusc.spring.boot.pojo;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Data
@ToString
@Component
public class Student {

    @Value("${person.id}")
    private int id;
    @Value("${person.name}")
    private String name;

}
