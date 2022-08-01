package cn.locusc.duo.jvm.part2.chapter2.codelist;

import java.util.HashSet;

/**
 * @author Jay
 * 运行时常量池导致的内存溢出异常
 * VM Args：-XX:PermSize=6M -XX:MaxPermSize=6M
 *
 * 由于运行时常量池是方法区的一部分，所以这两个区域的溢出测试可以放到一起进行。前面曾经
 * 提到HotSpot从JDK 7开始逐步“去永久代”的计划，并在JDK 8中完全使用元空间来代替永久代的背景
 * 故事，在此我们就以测试代码来观察一下，使用“永久代”还是“元空间”来实现方法区，对程序有什么
 * 实际的影响。
 *
 * String::intern()是一个本地方法，它的作用是如果字符串常量池中已经包含一个等于此String对象的
 * 字符串，则返回代表池中这个字符串的String对象的引用；否则，会将此String对象包含的字符串添加
 * 到常量池中，并且返回此String对象的引用。在JDK 6或更早之前的HotSpot虚拟机中，常量池都是分配
 * 在永久代中，我们可以通过-XX：PermSize和-XX：MaxPermSize限制永久代的大小，即可间接限制其
 * 中常量池的容量，具体实现如代码清单2-7所示，请读者测试时首先以JDK 6来运行代码。
 *
 * 运行结果：
 * Exception in thread "main" java.lang.OutOfMemoryError: PermGen space
 * at java.lang.String.intern(Native Method)
 * at org.fenixsoft.oom.RuntimeConstantPoolOOM.main(RuntimeConstantPoolOOM.java: 18)
 *
 * 从运行结果中可以看到，运行时常量池溢出时，在OutOfMemoryError异常后面跟随的提示信息
 * 是“PermGen space”，说明运行时常量池的确是属于方法区（即JDK 6的HotSpot虚拟机中的永久代）的
 * 一部分。
 *
 *
 * 而使用JDK 7或更高版本的JDK来运行这段程序并不会得到相同的结果，无论是在JDK 7中继续使
 * 用-XX：MaxPermSize参数或者在JDK 8及以上版本使用-XX：MaxMeta-spaceSize参数把方法区容量同
 * 样限制在6MB，也都不会重现JDK 6中的溢出异常，循环将一直进行下去，永不停歇[1]。出现这种变
 * 化，是因为自JDK 7起，原本存放在永久代的字符串常量池被移至Java堆之中，所以在JDK 7及以上版
 * 本，限制方法区的容量对该测试用例来说是毫无意义的。这时候使用-Xmx参数限制最大堆到6MB就能
 * 够看到以下两种运行结果之一，具体取决于哪里的对象分配时产生了溢出：
 *
 * // OOM异常一：
 * Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
 * at java.base/java.lang.Integer.toString(Integer.java:440)
 * at java.base/java.lang.String.valueOf(String.java:3058)
 * at RuntimeConstantPoolOOM.main(RuntimeConstantPoolOOM.java:12)
 * // OOM异常二：
 * Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
 * at java.base/java.util.HashMap.resize(HashMap.java:699)
 * at java.base/java.util.HashMap.putVal(HashMap.java:658)
 * at java.base/java.util.HashMap.put(HashMap.java:607)
 * at java.base/java.util.HashSet.add(HashSet.java:220)
 * at RuntimeConstantPoolOOM.main(RuntimeConstantPoolOOM.java from InputFile-Object:14)
 * 2022/7/29
 */
public class RuntimeConstantPoolOOM27 {

    public static void main(String[] args) {
        // 使用Set保持着常量池引用，避免Full GC回收常量池行为
        HashSet<String> set = new HashSet<>();
        short i = 0;
        // 在short范围内足以让6MB的PermSize产生OOM了
        while (true) {
            set.add(String.valueOf(i++).intern());
        }
    }

}
