package cn.locusc.netty.io.websocket.server;

import cn.locusc.netty.io.websocket.channels.WebSocketChannelInitializer;
import cn.locusc.netty.io.websocket.configuration.NettyConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Netty Websocket服务器
 */
@Component
public class Netty4NioWebSocketServer implements Runnable {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Resource
    private NettyConfiguration nettyConfiguration;

    @Resource
    private WebSocketChannelInitializer webSocketChannelInitializer;

    @Override
    public void run() {
        try {
            // 1.创建服务端启动助手
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 2.设置线程组
            serverBootstrap.group(bossGroup, workerGroup);
            // 3.设置参数
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(webSocketChannelInitializer);
            // 4.启动
            ChannelFuture channelFuture = serverBootstrap.bind(nettyConfiguration.getPort()).sync();
            System.out.println("--Netty服务端启动成功---");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void preDestroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
