package cn.locusc.spring.deep.analysis.chapter2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class BeanFactoryTest {

    @Test
    public void test() {
        // spring3.1废弃
        ClassPathResource classPathResource = new ClassPathResource("beanFactoryTest.xml");
        BeanFactory xmlBeanFactory = new XmlBeanFactory(classPathResource);
        MyTestBean myTestBean = (MyTestBean) xmlBeanFactory.getBean("aliasMyTestBean");

        BeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
        BeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) defaultListableBeanFactory);
        int i = xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);

        MyTestBean myTestBean1 = defaultListableBeanFactory.getBean("myTestBean", MyTestBean.class);
        System.out.println(myTestBean.getTestStr());
        System.out.println(myTestBean1.getTestStr());
    }
}
