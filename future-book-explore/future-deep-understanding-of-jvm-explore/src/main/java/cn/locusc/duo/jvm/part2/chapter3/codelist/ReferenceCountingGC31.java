package cn.locusc.duo.jvm.part2.chapter3.codelist;

/**
 * @author Jay
 * 代码清单3-1 引用计数算法的缺陷
 * testGC()方法执行后，objA和objB会不会被GC呢？
 * 2022/8/15
 */
public class ReferenceCountingGC31 {

    public Object instance = null;

    private static final int _1MB = 1024 * 1024;

    /**
     * 这个成员属性的唯一意义就是占点内存，以便在能在GC日志中看清楚是否有回收过
     */
    private byte[] bigSize = new byte[2 * _1MB];

    public static void testGC() {
        ReferenceCountingGC31 objA = new ReferenceCountingGC31();
        ReferenceCountingGC31 objB = new ReferenceCountingGC31();
        objA.instance = objB;
        objB.instance = objA;

        objA = null;
        objB = null;

        System.gc();
    }

    public static void main(String[] args) {
        testGC();
    }

}
