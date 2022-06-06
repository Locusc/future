package cn.locusc.dubbo.producer.execute;

import cn.locusc.dubbo.producer.configuration.DubboProducerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class DubboProducerExecute {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(DubboProducerConfig.class);

        applicationContext.start();

        System.in.read();
    }

}
