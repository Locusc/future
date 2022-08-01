package cn.locusc.duo.jvm.part2.chapter2.codelist;

/**
 * @author Jay
 * VM Args：-Xss2M （这时候不妨设大些，请在32位系统下运行）
 *
 * 创建线程导致内存溢出异常(Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread)
 *
 * 如果测试时不限于单线程，通过不断建立线程的方式，在HotSpot上也是可以产生内存溢出异常
 * 的，具体如代码清单2-6所示。但是这样产生的内存溢出异常和栈空间是否足够并不存在任何直接的关
 * 系，主要取决于操作系统本身的内存使用状态。甚至可以说，在这种情况下，给每个线程的栈分配的
 * 内存越大，反而越容易产生内存溢出异常。
 *
 * 原因其实不难理解，操作系统分配给每个进程的内存是有限制的，譬如32位Windows的单个进程
 * 最大内存限制为2GB。HotSpot虚拟机提供了参数可以控制Java堆和方法区这两部分的内存的最大值，
 * 那剩余的内存即为2GB（操作系统限制）减去最大堆容量，再减去最大方法区容量，由于程序计数器
 * 消耗内存很小，可以忽略掉，如果把直接内存和虚拟机进程本身耗费的内存也去掉的话，剩下的内存
 * 就由虚拟机栈和本地方法栈来分配了。因此为每个线程分配到的栈内存越大，可以建立的线程数量自
 * 然就越少，建立线程时就越容易把剩下的内存耗尽，代码清单2-6演示了这种情况。
 *
 * 注意 重点提示一下，如果读者要尝试运行上面这段代码，记得要先保存当前的工作，由于在
 * Windows平台的虚拟机中，Java的线程是映射到操作系统的内核线程上[1]，无限制地创建线程会对操
 * 作系统带来很大压力，上述代码执行时有很高的风险，可能会由于创建线程数量过多而导致操作系统
 * 假死。
 *
 * 出现StackOverflowError异常时，会有明确错误堆栈可供分析，相对而言比较容易定位到问题所
 * 在。如果使用HotSpot虚拟机默认参数，栈深度在大多数情况下（因为每个方法压入栈的帧大小并不是
 * 一样的，所以只能说大多数情况下）到达1000~2000是完全没有问题，对于正常的方法调用（包括不能
 * 做尾递归优化的递归调用），这个深度应该完全够用了。但是，如果是建立过多线程导致的内存溢
 * 出，在不能减少线程数量或者更换64位虚拟机的情况下，就只能通过减少最大堆和减少栈容量来换取
 * 更多的线程。这种通过“减少内存”的手段来解决内存溢出的方式，如果没有这方面处理经验，一般比
 * 较难以想到，这一点读者需要在开发32位系统的多线程应用时注意。也是由于这种问题较为隐蔽，从
 * JDK 7起，以上提示信息中“unable to create native thread”后面，虚拟机会特别注明原因可能是“possibly
 * out of memory or process/resource limits reached”。
 * 2022/7/29
 */
public class JavaVMStackOOM26 {

    private void dontStop() {
        while (true) {
        }
    }

    public void stackLeakByThread() {
        while (true) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    dontStop();
                }
            });

            thread.start();
        }
    }

    public static void main(String[] args) {
        JavaVMStackOOM26 javaVMStackOOM26 = new JavaVMStackOOM26();
        javaVMStackOOM26.stackLeakByThread();
    }

}
