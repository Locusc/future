package cn.locusc.microservice.auto.deliver.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // 使用RestTemplate模板对象进行远程调用
    // LoadBalanced ribbon会向RestTemplate添加一个拦截器, 根据配置的负载均衡算法进行调用
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
