package cn.locusc.spring.deep.analysis.chapter2.code;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class BeanFactoryTest {

    /**
     * 读取配置文件beanFactoryTest.xml
     * 根据beanFactoryTest.xml中的配置找到对应的类的配置, 并实例化
     * 调用实例化后的实例
     *
     * 完成预想的功能 至少需要三个类
     * ConfigReader: 用于读取及验证配置文件 然后放置在内存中
     * ReflectionUtil: 用于根据配置文件中的配置进行反射实例化
     * App: 用于完成整个逻辑的串联
     *
     * 解析流程查看{@code https://github.com/Locusc/spring-framework.git}
     * {@link XmlBeanFactory}
     */
    @Test
    public void testSimpleLoad() {
        // spring3.1废弃
        ClassPathResource classPathResource = new ClassPathResource("beanFactoryTest.xml");
        BeanFactory xmlBeanFactory = new XmlBeanFactory(classPathResource);
        MyTestBean myTestBean = (MyTestBean) xmlBeanFactory.getBean("aliasMyTestBean");

        System.out.println(myTestBean.getTestStr());
    }

    @Test
    public void testRecentLoad() {
        // spring3.1废弃
        ClassPathResource classPathResource = new ClassPathResource("beanFactoryTest.xml");
        BeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
        BeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) defaultListableBeanFactory);
        int i = xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);
        MyTestBean myTestBean1 = defaultListableBeanFactory.getBean("myTestBean", MyTestBean.class);
        System.out.println(myTestBean1.getTestStr());
    }

}
