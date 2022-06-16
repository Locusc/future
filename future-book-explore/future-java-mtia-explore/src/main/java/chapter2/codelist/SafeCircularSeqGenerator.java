package chapter2.codelist;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SafeCircularSeqGenerator implements CircularSeqGenerator21 {

    private short sequence = -1;

    private final Lock lock = new ReentrantLock();

    /**
     * 显式锁
     */
    public short nextSequenceExplicitLock() {
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

    /**
     * 内部锁
     */
    @Override
    public synchronized short nextSequence() {
        if (sequence >= 999) {
            sequence = 0;
        } else {
            sequence++;
        }
        return sequence;
    }

    // 相当于
    public short nextSequenceBlock() {
        synchronized (this) {
            if (sequence >= 999) {
                sequence = 0;
            } else {
                sequence++;
            }
            return sequence;
        }
    }

    public static synchronized void staticMethod() {
        // 在此访问共享数据
    }

    // 相当于
    public static void staticMethodBlock() {
        synchronized (SafeCircularSeqGenerator.class) {
            // ...
        }
    }

}
