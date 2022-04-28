package cn.locusc.netty.io.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Netty4ChatNioClientHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 通道读取就绪事件
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
    }

}
