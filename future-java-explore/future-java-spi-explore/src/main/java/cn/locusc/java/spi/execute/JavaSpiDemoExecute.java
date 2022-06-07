package cn.locusc.java.spi.execute;

import cn.locusc.java.spi.service.JavaSpiDemoService;

import java.util.ServiceLoader;

public class JavaSpiDemoExecute {

    public static void main(String[] args) {
        ServiceLoader<JavaSpiDemoService> loader = ServiceLoader.load(JavaSpiDemoService.class);
        loader.forEach(s -> {
            String message = s.sendMessage();
            System.out.println(message);
        });
    }

}
