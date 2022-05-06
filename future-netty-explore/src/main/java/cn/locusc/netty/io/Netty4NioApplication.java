package cn.locusc.netty.io;

import cn.locusc.netty.io.websocket.server.Netty4NioWebSocketServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class Netty4NioApplication implements CommandLineRunner {

    @Resource
    private Netty4NioWebSocketServer netty4NioWebSocketServer;

    public static void main(String[] args) {
        SpringApplication.run(Netty4NioApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(netty4NioWebSocketServer).start();
    }

}
