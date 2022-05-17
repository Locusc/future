package cn.locusc.rpc.netty.provider;

import cn.locusc.rpc.netty.provider.server.RpcNettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class RpcNettyServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RpcNettyServerApplication.class, args);
    }

    @Resource
    private RpcNettyServer rpcNettyServer;

    @Override
    public void run(String... args) {
        new Thread(() -> rpcNettyServer.startRpcNettyServer("127.0.0.1", 8899)).start();
    }

}
