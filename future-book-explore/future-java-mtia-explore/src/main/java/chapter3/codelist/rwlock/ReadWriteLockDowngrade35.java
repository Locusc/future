package chapter3.codelist.rwlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Jay
 * 读写锁的降级示例
 * ReentrantReadWriteLock所实现的读写锁是个可重入锁ReentrantReadWriteLock支持
 * 锁的降级(Downgrade), 即一个线程持有读写锁的写锁的情况下可以继续获得相应的读锁.
 *
 * 锁的降级的反面是锁的升级(Upgrade), 即一个线程在持有读写锁的读锁的情况下,
 * 申请相应的写锁ReentrantReadWriteLock并不支持锁的升级,读线程如果要转而申请写
 * 锁, 需要先释放读锁, 然后申请相应的写锁
 * 2022/6/14
 */
public class ReadWriteLockDowngrade35 {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    public void operationWithLockDowngrade() {
        // 判断是否已经获取到了读锁
        boolean readLockAcquired = false;
        try {
            // 对共享数据进行更新
            // ...
            // 当前线程在持有写锁的情况下申请读锁readLock
            readLock.lock();
            readLockAcquired = true;
        } finally {
            writeLock.unlock();// 释放写锁
        }

        if (readLockAcquired) {
            try {
                // 读取共享数据并据此执行其他操作
                // ...
            } finally {
                readLock.unlock();// 释放读锁
            }
        } else {
            // ...
        }
    }

}
