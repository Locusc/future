package cn.locusc.mtia.chapter3.codelist;

/**
 * @author Jay
 * final关键字的作用示例
 *
 * 我们知道由于重排序的作用（参见清单2-10 )，一个线程读取到一个对象引用时，该对象可能尚未初始化完毕，
 * 即这些线程可能读取到该对象字段的默认值而不是初始值(通过构造器或者初始化语句指定的值)。
 * 在多线程环境下final关键字有其特殊的作用:
 *
 * 当一个对象被发布到其他线程的时候，该对象的所有final字段（实例变量）都是初始化完毕的，
 * 即其他线程读取这些字段的时候所读取到的值都是相应字段的初始值(而不是默认值)。
 * 而非 final字段没有这种保障，即这些线程读取该对象的非 final字段时所读取到的值可能仍然是相应字段的默认值。
 * 对于引用型final字段，final关键字还进一步确保该字段所引用的对象已经初始化完毕,即这些线程读取该字段所引用的对象的各个字段时所读取到的值都是相应字段的初始值。
 *
 * 假设两个线程分别执行清单3-27中的 writer()和 reader()，那么reader()的执行线程读取到实例变量x的值一定为1，
 * 而该线程读取到实例变量y的值则可能是2(初始值）也可能是0（默认值)。
 *
 * 2022/7/12
 */
public class FinalFieldExample {

    final int x;
    int y;
    static FinalFieldExample instance;

    public FinalFieldExample() {
        x = 1;
        y = 2;
    }

    public static void writer() {
        instance = new FinalFieldExample();
    }

    /**
     * 在JIT编译器的内联( Inline )优化的作用下, FinalFieldExample方法中的语句会被“挪入”writer方法，
     * 因此 writer方法对应的指令可能被编译为与如下伪代码等效的代码:
     * objRef = allocate(FinalFieldExample.class); //子操作①:分配对象所需的存储空间
     * objRef.x = 1; //子操作②:对象初始化
     * objRef.y = 2; //子操作③:对象初始化
     * instance = objRef; //子操作④:将对象引用写入共享变量
     * 其中，子操作③(非 final字段初始化)可能被JIT 编译器、处理器重排序到子操作④(对象发布)之后,
     * 因此当其他线程通过共享变量 instance看到对象引用objRef 的时候，
     * 该对象的实例变量y可能还没有被初始化(因为此时子操作③可能尚未被执行或者其结果尚未对其他处理器可见)，
     * 即这些线程看到的FinalFieldExample对象的y字段的值可能仍然是其默认值0。而FinalFieldExample的字段x则是采用final关键字修饰，
     * 因此Java 虚拟机会将子操作②( final字段初始化）限定在子操作④前完成。
     * 这里所谓的限定是指JIT编译器不会将构造器中对final字段的赋值操作重排到子操作④之后，并且还会禁止处理器做这种重排序1%。
     * 通过这种限定，Java虚拟机、处理器一起保障了对象instance被发布前其final字段x必然是初始化完毕的。
     */
    public static void reader() {
        final FinalFieldExample theInstance = instance;
        if (theInstance != null) {
            int diff = theInstance.y - theInstance.x;
            // diff的值可能为1(=2-1），也可能为-1（=0-1）。
            print(diff);
        }
    }

    private static void print(int x) {
        // ...
    }

}
