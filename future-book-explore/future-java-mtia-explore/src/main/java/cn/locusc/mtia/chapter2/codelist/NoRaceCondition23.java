package cn.locusc.mtia.chapter2.codelist;

public class NoRaceCondition23 {

    public int nextSequence(int sequence) {

        // 以下语句使用的是局部变量而非状态变量，并不会产生竞态
        if (sequence >= 999) {
            sequence = 0;
        } else {
            sequence++;
        }
        return sequence;
    }

}
