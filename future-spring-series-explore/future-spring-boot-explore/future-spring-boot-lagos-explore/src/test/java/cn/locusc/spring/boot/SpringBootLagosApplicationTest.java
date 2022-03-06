package cn.locusc.spring.boot;

import cn.locusc.spring.boot.controller.HelloController;
import cn.locusc.spring.boot.pojo.MyProperties;
import cn.locusc.spring.boot.pojo.Person;
import cn.locusc.spring.boot.pojo.Student;
import cn.locusc.spring.boot.starter.pojo.SimpleBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// 标记该类为springboot单元测试类 并加载项目的applicationContext上下文环境
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class) // 测试启动器 并加载spring boot测试注解
public class SpringBootLagosApplicationTest {

    @Autowired
    private HelloController helloController;

    @Autowired
    private Person person;

    @Autowired
    private Student student;

    @Autowired
    private MyProperties myProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SimpleBean simpleBean;

    @Value("${tom.description}")
    private String description;

    @Test
    public void contextLoads() {
        String demo = helloController.demo();
        System.out.println(demo);
    }

    @Test
    public void configurationTest() {
        System.out.println(person);
    }

    @Test
    public void studentTest() {
        System.out.println(student);
    }

    @Test
    public void myPropertiesTest() {
        System.out.println(myProperties);
    }

    @Test
    public void iocTest() {
        System.out.println(applicationContext.containsBean("myService"));
    }

    @Test
    public void placeholderTest() {
        System.out.println(description);
    }

    @Test
    public void starterTest() {
        System.out.println(simpleBean);
    }

}
