package cn.locusc.netty.io.websocket.channels;

import cn.locusc.netty.io.websocket.configuration.NettyConfiguration;
import cn.locusc.netty.io.websocket.handlers.WebSocketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * websocket 通道初始化对象
 */
@Component
public class WebSocketChannelInitializer extends ChannelInitializer {

    @Resource
    private NettyConfiguration nettyConfiguration;

    @Resource
    private WebSocketHandler webSocketHandler;


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 对http协议的支持.
        pipeline.addLast(new HttpServerCodec());
        // 对大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        // post请求分三部分. request line / request header / message body
        // HttpObjectAggregator将多个信息转化成单一的request或者response对象
        pipeline.addLast(new HttpObjectAggregator(8000));
        // 将http协议升级为ws协议. websocket的支持
        pipeline.addLast(new WebSocketServerProtocolHandler(nettyConfiguration.getPath()));
        // 自定义处理handler
        pipeline.addLast(webSocketHandler);
    }

}
