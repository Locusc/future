package cn.locusc.duo.jvm.part2.chapter2.codelist;

import java.util.ArrayList;

/**
 * @author Jay
 * VM Args：-Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * Java堆内存溢出异常测试
 *
 * 2.4.1 Java堆溢出
 * Java堆用于储存对象实例，我们只要不断地创建对象，并且保证GCRoots到对象之间有可达路径来避免垃圾回收机制清除这些对象，
 * 那么随着对象数量的增加，总容量触及最大堆的容量限制后就会产生内存溢出异常。
 * 代码清单2-3中限制Java堆的大小为20MB，不可扩展（将堆的最小值-Xms参数与最大值-Xmx参数设置为一样即可避免堆自动扩展)，
 * 通过参数-XX:+HeapDumpOnOutOf-MemoryError可以让虚拟机在出现内存溢出异常的时候Dump出当前的内存堆转储快照以便进行事后分析[1]。
 *
 * Java堆内存的OutOfMemoryError异常是实际应用中最常见的内存溢出异常情况。
 * 出现Java堆内存溢出时，异常堆栈信息"java.lang OutOfMemoryError”会跟随进一步提示“Java heap space"'。
 *
 * 要解决这个内存区域的异常，常规的处理方法是首先通过内存映像分析工具（如Eclipse MemoryAnalyzer）对Dump出来的堆转储快照进行分析。
 * 第一步首先应确认内存中导致OOM的对象是否是必要的，也就是要先分清楚到底是出现了内存泄漏（Memory Leak〉还是内存溢出(Memory
 * Overflow)。图2-5显示了使用Eclipse Memory Analyzer打开的堆转储快照文件。
 *
 * 如果是内存泄漏，可进一步通过工具查看泄漏对象到GC Roots的引用链，找到泄漏对象是通过怎样的引用路径、与哪些GC Roots相关联，
 * 才导致垃圾收集器无法回收它们，根据泄漏对象的类型信息以及它到GC Roots引用链的信息，一般可以比较准确地定位到这些对象创建的位置，
 * 进而找出产生内存泄漏的代码的具体位置。
 *
 * 如果不是内存泄漏，换句话说就是内存中的对象确实都是必须存活的，那就应当检查Java虚拟机的堆参数（-Xmx与-Xms）设置，与机器的内存对比，
 * 看看是否还有向上调整的空间。再从代码上检查是否存在某些对象生命周期过长、持有状态时间过长、存储结构设计不合理等情况，尽量减少程序运行期的内存消耗。
 *
 * 以上是处理Java堆内存问题的简略思路，处理这些问题所需要的知识、工具与经验是后面三章的主题，
 * 后面我们将会针对具体的虚拟机实现、具体的垃圾收集器和具体的案例来进行分析，这里就先暂不展开。
 * [1]关于堆转储快照文件分析方面的内容，可参见第4章。
 * 2022/7/29
 */
public class HeapOOM23 {


    static class OOMObject {
    }

    public static void main(String[] args) {
        ArrayList<OOMObject> oomObjects = new ArrayList<>();

        while (true) {
            oomObjects.add(new OOMObject());
        }
    }

}
