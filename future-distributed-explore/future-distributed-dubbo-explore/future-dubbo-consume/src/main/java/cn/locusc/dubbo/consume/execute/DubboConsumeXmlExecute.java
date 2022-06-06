package cn.locusc.dubbo.consume.execute;

import cn.locusc.dubbo.api.service.DubboDemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DubboConsumeXmlExecute {

    public static void main(String[] args) throws  Exception{
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:dubbo-comsumer.xml");

        DubboDemoService dubboDemoService =
                applicationContext.getBean("dubboDemoService", DubboDemoService.class);

        System.in.read();
        String result = dubboDemoService.notifyMessage("dubbo demo test");
        System.out.println(result);
    }

}
