package cn.locusc.mtia.chapter3.codelist;

/**
 * @author Jay
 * 基于 volatile 的简易读写锁
 * 2022/7/4
 */
public class Counter37 {

    private volatile long count;

    public long value() {
        return count;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = "VO_VOLATILE_INCREMENT",
            justification = "It is done inside critical section")
    public void increment() {
        synchronized (this) {
            count++;
        }
    }

}
