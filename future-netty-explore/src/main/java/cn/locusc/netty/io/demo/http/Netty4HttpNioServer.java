package cn.locusc.netty.io.demo.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class Netty4HttpNioServer {

    //端口号
    private int port;

    public Netty4HttpNioServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        // 1. 创建bossGroup线程组: 处理网络事件--连接事件
        EventLoopGroup bossGroup = null;
        // 2. 创建workerGroup线程组: 处理网络事件--读写事件 2*处理器线程数
        EventLoopGroup workerGroup = null;

        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            // 3. 创建服务端启动助手
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 4. 设置bossGroup线程组和workerGroup线程组
            serverBootstrap.group(bossGroup, workerGroup)
                    // 5. 设置服务端通道实现为NIO
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 8. 向pipeline中添加自定义业务处理handler
                            // 添加编解码器
                            ch.pipeline().addLast(new HttpServerCodec());
                            // 自定义业务处理类
                            ch.pipeline().addLast(new Netty4HttpNioServerHandler());
                        }
                    });

            // 9. 启动服务端并绑定端口,同时将异步改为同步
            ChannelFuture future = serverBootstrap.bind(port);
            future.addListener((ChannelFutureListener) cfl -> {
                if(cfl.isSuccess()) {
                    System.out.println("端口绑定成功!");
                } else {
                    System.out.println("端口绑定失败!");
                }
            });
            // 10. 关闭通道(并不是真正意义上关闭,而是监听通道关闭的状态)和关闭连接池
            System.out.println("http服务端启动成功.");
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Netty4HttpNioServer netty4HttpNioServer = new Netty4HttpNioServer(10014);
        netty4HttpNioServer.run();
    }

}
