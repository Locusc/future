package cn.locusc.dubbo.consume.execute;

import cn.locusc.dubbo.consume.components.DubboConsumeComponent;
import cn.locusc.dubbo.consume.configuration.DubboConsumeConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class DubboConsumeExecute {

    public static void main(String[] args) throws IOException {
        System.out.println("-------------");
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(DubboConsumeConfig.class);

        applicationContext.start();

        // 获取消费者组件
        DubboConsumeComponent dubboConsumeComponent = applicationContext.getBean(DubboConsumeComponent.class);
        while(true){
            System.in.read();
            String message = dubboConsumeComponent.notifyMessage("dubbo demo test");
            System.out.println(String.format("result: %s", message));
        }
    }

}
