package chapter8.codelist;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jay
 * 控制线程的暂挂与恢复的工具类
 *
 * Thread.suspend()、Thread.resume()这两个方法都是已废弃的方法。其作用分别是暂挂线程和恢复线程。
 * 暂挂(Suspend )与暂停的含义基本相同，它更多的是指用户(人）感知得到的线程暂停;
 * 恢复(Resume）与唤醒的含义基本相同，它更多的是指用户(人)感知得到的线程唤醒。
 * 我们可以采用与停止线程相似的思想来实现线程的暂挂与恢复:设置一个线程暂挂标志，
 * 线程每次执行比较耗时的操作前都先检查一下这个标志。如果该标志指示线程应该暂挂，
 * 那么线程就执行Object.wait()/Condition.await()暂停,直到其他线程重新设置暂挂标志并将其唤醒。
 * 根据该思路，我们可以设计一个用于控制线程的暂挂与恢复的工具类 PauseControl，如清单8-4所示。
 *
 * PauseControl本身继承自ReentrantLock,其 volatile实例变量suspended充当线程暂挂标记。
 * PauseControl.requestPause()的作用仅仅是将suspended 置为true，
 * 而 PauseControl.pauseIfNeccessary()则通过Condition.await()确保只有在 suspended 不为true的情况下指定的目标动作才会被执行。
 * PauseControl.proceed()的作用是将 suspended置为 false并唤醒所有被暂停的线程。
 * PauseControl.requestPause()、PauseControl.proceed()的作用分别相当于Thread.suspend()、Thread.resume()。
 *
 * 2022/7/20
 */
public class PauseControl84 extends ReentrantLock {

    private static final long serialVersionUID = 176912639934052187L;
    // 线程暂挂标志
    private volatile boolean suspended = false;

    private final Condition condSuspended = newCondition();

    /**
     * 暂停线程
     */
    public void requestPause() {
        suspended = true;
    }

    /**
     * 恢复线程
     */
    public void proceed() {
        lock();
        try {
            suspended = false;
            condSuspended.signalAll();
        } finally {
            unlock();
        }
    }

    /**
     * 当前线程仅在线程暂挂标记不为true的情况下才执行指定的目标动作。
     *
     * @targetAction 目标动作
     * @throws InterruptedException
     */
    public void pauseIfNecessary(Runnable targetAction) throws InterruptedException {
        lock();
        try {
            while (suspended) {
                condSuspended.await();
            }
            targetAction.run();
        } finally {
            unlock();
        }
    }
}
