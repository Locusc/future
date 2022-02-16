package cn.locusc.spring.ioc.logos.pojo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author 应癫
 */
// @Lazy 注解开启延迟加载
public class Result implements BeanNameAware,
        BeanFactoryAware, ApplicationContextAware,
        InitializingBean, DisposableBean {

    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public void setBeanName(String s) {
        System.out.println("注册我称为bean时定义的id: " + s);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // 在具体bean的类中获取外部环境的工厂
        System.out.println("管理我的beanFactory为: " + beanFactory);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("高级容器接口ApplicationContext: " + applicationContext);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet....");
    }

    public void initMethod() throws Exception {
        System.out.println("init-method....");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("@PostConstruct....");
    }

    @PreDestroy
    public void preDestroy() throws Exception {
        System.out.println("PreDestroy....");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy....");
    }
}
