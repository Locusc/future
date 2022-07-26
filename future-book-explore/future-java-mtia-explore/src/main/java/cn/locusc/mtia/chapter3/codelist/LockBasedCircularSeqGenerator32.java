package cn.locusc.mtia.chapter3.codelist;

import cn.locusc.mtia.chapter2.codelist.CircularSeqGenerator21;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jay
 * 使用显式锁实现循环递增序列号生成器
 * 2022/6/14
 */
public class LockBasedCircularSeqGenerator32 implements CircularSeqGenerator21 {

    private short sequence = -1;

    // 默认为不公平 公平适合持有时间长或者线程申请的平均时间相对长的情况
    private final Lock lock = new ReentrantLock();

    @Override
    public short nextSequence() {
        lock.lock();
        try {
            if (sequence >= 999) {
                sequence = 0;
            } else {
                sequence++;
            }
            return sequence;
        } finally {
            lock.unlock();
        }
    }

}
