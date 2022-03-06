package cn.locusc.spring.boot.starter.config;

import cn.locusc.spring.boot.starter.pojo.SimpleBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SimpleBean.class) // 当类路径classpath下有指定的类的情况, 就会进行自动配置
public class MyAutoConfiguration {

    static {
        System.out.println("MyAutoConfiguration init...");
    }

    @Bean
    public SimpleBean simpleBean() {
        return new SimpleBean();
    }

}
