package chapter3.codelist.rwlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Jay
 * 读写锁使用方法
 *
 * 只读操作比写（更新）操作要频繁得多;
 * 读线程持有锁的时间比较长;
 * 2022/6/14
 */
public class ReadWriteLockUsage34 {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    // 读线程执行该方法
    public void reader() {
        readLock.lock(); // 申请读锁
        try {
            // 在此区域读取共享变量
        } finally {
            readLock.unlock();// 总是在finally块中释放锁，以免锁泄漏
        }
    }

    // 写线程执行该方法
    public void writer() {
        writeLock.lock(); // 申请读锁
        try {
            // 在此区域访问（读、写）共享变量
        } finally {
            writeLock.unlock();// 总是在finally块中释放锁，以免锁泄漏
        }
    }
}
