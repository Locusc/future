package cn.locusc.dubbo.consume.execute;

import cn.locusc.dubbo.api.service.DubboDemoService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.Future;

public class DubboConsumeXmlExecute {

    public static void main(String[] args) throws  Exception{
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:dubbo-comsume.xml");

        DubboDemoService dubboDemoService =
                applicationContext.getBean("dubboDemoService", DubboDemoService.class);

        System.in.read();
        String result = dubboDemoService.notifyMessage("dubbo demo test");
        System.out.println(result);

        // 获取异步执行结果
        // Future<Object> future = RpcContext.getContext().getFuture();
        // System.out.println(future.get());
    }

}
