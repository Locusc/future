package cn.locusc.netty.io.nio.buffer;

import java.nio.ByteBuffer;

/**
 * @author Jay
 * 缓冲区实例
 * 2022/4/8
 */
public class CreateBufferDemo {

    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(5);
        for (int i = 0; i < 5; i++) {
            //从缓冲区当中拿去数据
            System.out.println(allocate.get());
        }

        //会报错. 后续讲解
        //System.out.println(allocate.get());//从缓冲区当中拿去数据

        //2.创建一个有内容的缓冲区
        ByteBuffer wrap = ByteBuffer.wrap("locusc".getBytes());
        for (int i = 0; i < 5; i++) {
            System.out.println(wrap.get());
        }
    }

}
