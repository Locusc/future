package cn.locusc.duo.jvm.part2.chapter2.codelist;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author Jay
 * VM Args：-Xmx20M -XX:MaxDirectMemorySize=10M
 * 使用unsafe分配本机内存
 *
 * 在JDK 1.4中新加入了NIO（New Input/Output）类，引入了一种基于通道（Channel）与缓冲区
 * （Buffer）的I/O方式，它可以使用Native函数库直接分配堆外内存，然后通过一个存储在Java堆里面的
 * DirectByteBuffer对象作为这块内存的引用进行操作。这样能在一些场景中显著提高性能，因为避免了
 * 在Java堆和Native堆中来回复制数据
 *
 * 直接内存（Direct Memory）的容量大小可通过-XX：MaxDirectMemorySize参数来指定，如果不
 * 去指定，则默认与Java堆最大值（由-Xmx指定）一致，代码清单2-10越过了DirectByteBuffer类直接通
 * 过反射获取Unsafe实例进行内存分配（Unsafe类的getUnsafe()方法指定只有引导类加载器才会返回实
 * 例，体现了设计者希望只有虚拟机标准类库里面的类才能使用Unsafe的功能，在JDK 10时才将Unsafe
 * 的部分功能通过VarHandle开放给外部使用），因为虽然使用DirectByteBuffer分配内存也会抛出内存溢
 * 出异常，但它抛出异常时并没有真正向操作系统申请分配内存，而是通过计算得知内存无法分配就会
 * 在代码里手动抛出溢出异常，真正申请分配内存的方法是Unsafe::allocateMemory()。
 *
 * 由直接内存导致的内存溢出，一个明显的特征是在Heap Dump文件中不会看见有什么明显的异常
 * 情况，如果读者发现内存溢出之后产生的Dump文件很小，而程序中又直接或间接使用了
 * DirectMemory（典型的间接使用就是NIO），那就可以考虑重点检查一下直接内存方面的原因了。
 * 2022/7/29
 */
public class DirectMemoryOOM210 {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true) {
            unsafe.allocateMemory(_1MB);
        }
    }

}
