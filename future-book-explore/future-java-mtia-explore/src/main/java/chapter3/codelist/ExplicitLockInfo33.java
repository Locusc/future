package chapter3.codelist;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jay
 * 演示线程转储显式锁信息的示例程序
 *
 * 获取线程转储的时候需要指定 jstack "-l" 参数才能使产生的线程转储包含显式锁的相关信息
 * Linux系统中使用"kill -3 Java进程PID", 命令所产生的线程转储并不会显示某个显式锁实例是被
 * 哪个线程持有的
 * 2022/6/14
 */
public class ExplicitLockInfo33 {

    private static final ReentrantLock lock = new ReentrantLock();

    private static int shareData = 0;

    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    try {
                        Thread.sleep(2200000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    shareData = 1;
                } finally {
                    lock.unlock();
                }
            }
        });

        thread.start();
        Thread.sleep(100);
        lock.lock();
        try {
            System.out.println("shareData:" + shareData);
        } finally {
            lock.unlock();
        }

        // 用于检测相应锁是否被某个线程持有
        // lock.isLocked();
        // 可用于检查相应锁的等待线程的数量
        // lock.getQueueLength();
        // Thread.holdsLock(); 检测当前线程是否持有指定的内部锁
    }

}
