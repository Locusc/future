package cn.locusc.duo.jvm.part2.chapter2.codelist;

/**
 * @author Jay
 * VM Args：-Xss128k
 * 使用-Xss参数减少栈内存容量。
 *
 * 由于HotSpot虚拟机中并不区分虚拟机栈和本地方法栈，因此对于HotSpot来说，-Xoss参数（设置
 * 本地方法栈大小）虽然存在，但实际上是没有任何效果的，栈容量只能由-Xss参数来设定。关于虚拟
 * 机栈和本地方法栈，在《Java虚拟机规范》中描述了两种异常：
 * 1）如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出StackOverflowError异常。
 * 2）如果虚拟机的栈内存允许动态扩展，当扩展栈容量无法申请到足够的内存时，将抛出
 * OutOfMemoryError异常。
 *
 * 《Java虚拟机规范》明确允许Java虚拟机实现自行选择是否支持栈的动态扩展，而HotSpot虚拟机
 * 的选择是不支持扩展，所以除非在创建线程申请内存时就因无法获得足够内存而出现
 * OutOfMemoryError异常，否则在线程运行时是不会因为扩展而导致内存溢出的，只会因为栈容量无法
 * 容纳新的栈帧而导致StackOverflowError异常。
 *
 * 为了验证这点，我们可以做两个实验，先将实验范围限制在单线程中操作，尝试下面两种行为是
 * 否能让HotSpot虚拟机产生OutOfMemoryError异常：
 *
 * ·使用-Xss参数减少栈内存容量。
 * 结果：抛出StackOverflowError异常，异常出现时输出的堆栈深度相应缩小。
 * ·定义了大量的本地变量，增大此方法帧中本地变量表的长度。
 * 结果：抛出StackOverflowError异常，异常出现时输出的堆栈深度相应缩小。
 * 首先，对第一种情况进行测试，具体如代码清单2-4所示。
 * 代码清单2-4 虚拟机栈和本地方法栈测试（作为第1点测试程序）
 *
 * 对于不同版本的Java虚拟机和不同的操作系统，栈容量最小值可能会有所限制，这主要取决于操
 * 作系统内存分页大小。譬如上述方法中的参数-Xss128k可以正常用于32位Windows系统下的JDK 6，但
 * 是如果用于64位Windows系统下的JDK 11，则会提示栈容量最小不能低于180K，而在Linux下这个值则
 * 可能是228K，如果低于这个最小限制，HotSpot虚拟器启动时会给出如下提示：
 * The Java thread stack size specified is too small. Specify at least 228k
 * 2022/7/29
 */
public class JavaVMStackSOF24_1 {

    private int stackLength = 1;

    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackSOF24_1 javaVMStackSOF241 = new JavaVMStackSOF24_1();
        try {
            javaVMStackSOF241.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length:" + javaVMStackSOF241.stackLength);
            throw e;
        }
    }

}
