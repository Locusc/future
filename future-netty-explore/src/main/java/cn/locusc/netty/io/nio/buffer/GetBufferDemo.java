package cn.locusc.netty.io.nio.buffer;

import java.nio.ByteBuffer;

/**
 * @author Jay
 * 从缓冲区中读取数据
 * 2022/4/8
 */
public class GetBufferDemo {

    public static void main(String[] args) {
        //1.创建一个指定长度的缓冲区
        ByteBuffer allocate = ByteBuffer.allocate(10);
        allocate.put("0123".getBytes());
        // 当前位置
        System.out.println("position:" + allocate.position());//4
        // 范围
        System.out.println("limit:" + allocate.limit());//10
        // 总通量
        System.out.println("capacity:" + allocate.capacity());//10
        // 剩余容量
        System.out.println("remaining:" + allocate.remaining());//6

        //切换读模式
        System.out.println("读取数据--------------");
        allocate.flip();
        System.out.println("position:" + allocate.position());//4
        System.out.println("limit:" + allocate.limit());//10
        System.out.println("capacity:" + allocate.capacity());//10
        System.out.println("remaining:" + allocate.remaining());//6
        for (int i = 0; i < allocate.limit(); i++) {
            System.out.println(allocate.get());
        }

        //读取完毕后.继续读取会报错,超过limit值
        //System.out.println(allocate.get());

        //读取指定索引字节
        // (获取索引不报错是因为指定了获取位置, 如果直接get()会以当前的position为准)
        System.out.println("读取指定索引字节--------------");
        System.out.println(allocate.get(1));

        System.out.println("读取多个字节--------------");
        // 重复读取
        allocate.rewind();
        byte[] bytes = new byte[4];
        allocate.get(bytes);
        System.out.println(new String(bytes));

        // 将缓冲区转化字节数组返回
        System.out.println("将缓冲区转化字节数组返回--------------");
        byte[] array = allocate.array();
        System.out.println(new String(array));

        // 切换写模式,覆盖之前索引所在位置的值
        System.out.println("写模式--------------");
        allocate.clear();
        allocate.put("abc".getBytes());
        System.out.println(new String(allocate.array()));
    }

}
