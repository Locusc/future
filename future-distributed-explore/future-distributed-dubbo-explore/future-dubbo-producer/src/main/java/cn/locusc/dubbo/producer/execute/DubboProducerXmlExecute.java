package cn.locusc.dubbo.producer.execute;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DubboProducerXmlExecute {

    public static void main(String[] args) throws  Exception{
        ClassPathXmlApplicationContext applicationContext
                = new ClassPathXmlApplicationContext("classpath:dubbo-provider.xml");
        applicationContext.start();
        System.in.read();
    }

}
