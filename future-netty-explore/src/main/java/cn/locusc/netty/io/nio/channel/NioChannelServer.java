package cn.locusc.netty.io.nio.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Jay
 * 通道和缓冲区, 不包含选择器
 * 服务端
 * 2022/4/8
 */
public class NioChannelServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        //1. 打开一个服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2. 绑定对应的端口号
        serverSocketChannel.bind(new InetSocketAddress(9999));
        //3. 通道默认是阻塞的，需要设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        for (;;) {
            //4. 检查是否有客户端连接 有客户端连接会返回对应的通道
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) {
                System.out.println("没有客户端连接...我去做别的事情");
                Thread.sleep(2000);
                continue;
            }

            //5. 获取客户端传递过来的数据,并把数据放在byteBuffer这个缓冲区中
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            //返回值
            //正数: 表示本地读到有效字节数
            //0: 表示本次没有读到数据
            //-1: 表示读到末尾
            int read = socketChannel.read(allocate);
            System.out.println("客户端消息:" + new String(allocate.array(), 0,
                    read, StandardCharsets.UTF_8));

            //6. 给客户端回写数据
            socketChannel.write(ByteBuffer.wrap("have no money".getBytes(StandardCharsets.UTF_8)));

            //7. 释放资源
            socketChannel.close();
        }
    }

}
