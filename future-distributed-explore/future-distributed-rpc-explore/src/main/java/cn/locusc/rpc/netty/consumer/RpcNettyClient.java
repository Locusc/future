package cn.locusc.rpc.netty.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 客户端
 * 1.连接Netty服务端
 * 2.提供给调用者主动关闭资源的方法
 * 3.提供消息发送的方法
 */
public class RpcNettyClient {

    private EventLoopGroup eventLoopGroup;

    private Channel channel;

    private final String ip;

    private final int port;

    private final RpcNettyClientHandler rpcNettyClientHandler = new RpcNettyClientHandler();

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public RpcNettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        initRpcNettyClient();
    }

    /**
     * 初始化方法-连接Netty服务端
     */
    public void initRpcNettyClient() {
        try {
            // 1.创建线程组
            eventLoopGroup = new NioEventLoopGroup();
            // 2.创建启动助手
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //String类型编解码器
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            //添加客户端处理类
                            pipeline.addLast(rpcNettyClientHandler);
                        }
                    });

            channel = bootstrap.connect(ip, port).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提供给调用者主动关闭资源的方法
     */
    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }


    /**
     * 提供消息发送的方法
     */
    public Object send(String msg) throws ExecutionException, InterruptedException {
        rpcNettyClientHandler.setRequestMsg(msg);
        Future<Object> submit = executorService.submit(rpcNettyClientHandler);
        return submit.get();
    }

}
