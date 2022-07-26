package cn.locusc.mtia.chapter10.codelist;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class Counter103 {

    private volatile long count;

    public long vaule() {
        return count;
    }

    @SuppressFBWarnings(value = "VO_VOLATILE_INCREMENT",
            justification = "此处特意不加锁，以便测试代码能够报告相应错误")
    public void increment() {
        // 此处特意不加锁，以便测试代码能够报告相应错误
        count++;
    }

}
