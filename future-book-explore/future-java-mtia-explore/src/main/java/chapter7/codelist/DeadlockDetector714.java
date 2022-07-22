package chapter7.codelist;

import utils.Debug;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author Jay
 * 前面我们介绍的是如何在代码这一层规避死锁的产生，即防患于未然，那么万一死锁已然产生，如何将其解除呢?这就是死锁的故障恢复问题。
 * 如果代码中使用的是内部锁或者使用的是显式锁而锁的申请是通过Lock.lock()调用实现的,那么这些锁的使用所导致的死锁故障是不可恢复的，
 * 而我们唯一能够做的就是重启Java 虚拟机。如果代码中使用的是显式锁且锁的申请是通过Lock.lockInterruptibly()调用实现的,
 * 那么这些锁的使用所导致的死锁理论上是可恢复的，
 * 但是，死锁的恢复实际可操作性并不强——进行恢复的尝试可能是徒劳的（故障线程可无法响应中断)且有害的（可能导致其他线程活性故障)!尽管如此，
 * 我们仍然探讨这个问题，是因为它有助于我们进一步理解线程的中断机制。
 *
 * 注意
 * 由于导致死锁的线程的不可控性（比如第三方软件启动的线程)，因此死锁恢复的实际可操作性并不强:
 * 对死锁进行的故障恢复尝试可能是徒劳的（故障线程可无法响应中断）且有害的（可能导致活锁等问题)。
 *
 * 死锁的自动恢复有赖于线程的中断机制，其基本思想是:定义一个工作者线程DeadlockDetector专门用于死锁检测与恢复，如清单7-14所示。
 * 该线程定期检测系统中是否存在死锁，若检测到死锁，则随机选取一个死锁线程并给其发送中断。
 * 该中断使得一个任意的死锁线程（目标线程）被Java虚拟机唤醒，从而使其抛出InterruptedException异常。
 * 这使得目标线程不再等待它本来永远也无法申请到的资源，从而破坏了死锁产生的必要条件中的“占用并等待资源”中的“等待资源”部分。
 * 目标线程则通过对InterruptedException进行处理的方式来响应中断:目标线程捕获InterruptedException异常后将其已经持有的资源（锁）主动释放掉，
 * 这相当于破坏了死锁产生的必要条件中的“占用并等待资源”中的“占用资源”部分。接着，DeadlockDetector继续检测系统中是否仍然存在死锁，
 * 若存在，则继续选中一个任意的死锁线程并给其发送中断，直到系统中不再存在死锁。
 *
 * DeadlockDetector是通过java.lang.management.ThreadMXBean.findDeadlockedThreads()调用来实现死锁检测的。
 * ThreadMXBean.findDeadlockedThreads()能够返回一组死锁线程的线程编号。
 * ThreadMXBean类是JMX ( Java Management Extension ) API的一部分，因此其提供的功能也可以通过jconsole、jvisualvm手工调用。
 *
 * 2022/7/19
 */
public class DeadlockDetector714 extends Thread {

    static final ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
    /**
     * 检测周期（单位为毫秒）
     */
    private final long monitorInterval;

    public DeadlockDetector714(long monitorInterval) {
        super("DeadlockDetector714");
        setDaemon(true);
        this.monitorInterval = monitorInterval;
    }

    public DeadlockDetector714() {
        this(2000);
    }

    public static ThreadInfo[] findDeadlockedThreads() {
        long[] ids = tmb.findDeadlockedThreads();
        return null == tmb.findDeadlockedThreads() ?
                new ThreadInfo[0] : tmb.getThreadInfo(ids);
    }

    public static Thread findThreadById(long threadId) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getId() == threadId) {
                return thread;
            }
        }
        return null;
    }

    public static boolean interruptThread(long threadID) {
        Thread thread = findThreadById(threadID);
        if (null != thread) {
            thread.interrupt();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        ThreadInfo[] threadInfoList;
        ThreadInfo ti;
        int i = 0;
        try {
            for (;;) {
                // 检测系统中是否存在死锁
                threadInfoList = DeadlockDetector714.findDeadlockedThreads();
                if (threadInfoList.length > 0) {
                    // 选取一个任意的死锁线程
                    ti = threadInfoList[i++ % threadInfoList.length];
                    Debug.error("Deadlock detected,trying to recover"
                                    + " by interrupting%n thread(%d,%s)%n",
                            ti.getThreadId(),
                            ti.getThreadName());
                    // 给选中的死锁线程发送中断
                    DeadlockDetector714.interruptThread(ti.getThreadId());
                    continue;
                } else {
                    Debug.info("No deadlock found!");
                    i = 0;
                }
                Thread.sleep(monitorInterval);
            }// for循环结束
        } catch (InterruptedException e) {
            // 什么也不做
            ;
        }
    }

}
