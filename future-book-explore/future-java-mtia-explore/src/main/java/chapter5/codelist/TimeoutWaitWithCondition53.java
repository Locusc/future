package chapter5.codelist;

import utils.Debug;
import utils.Tools;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jay
 * 使用条件变量实现等待超时控制
 *
 * 可见，应用代码是这样解决过早唤醒问题的:在应用代码这一层次上建立保护条件与条件变量之间的对应关系，
 * 即让使用不同保护条件的等待线程调用不同的条件变量的await方法来实现其等待;
 * 并让通知线程在更新了共享变量之后，仅调用涉及了这些共享变量的保护条件所对应的条件变量的signal/signalAll方法来实现通知。
 *
 * 注意
 * Condition接口本身只是对解决过早唤醒问题提供了支持。
 * 要真正解决过早唤醒问题，我们需要通过应用代码维护保护条件与条件变量之间的对应关系，
 * 即使用不同的保护条件的等待线程需要调用不同的条件变量的await方法来实现其等待，
 * 并使通知线程在更新了相关共享变量之后，仅调用与这些共享变量有关的保护条件所对应的条件变量的signal/signalAll方法来实现通知。
 *
 * Condition接口还解决了Object.wait(long)存在的问题——Object.wait(long)无法区分其返回是由于等待超时还是被通知的。
 * Condition.awaitUntil(Date deadline)可以用于实现带超时时间限制的等待,
 * 并且该方法的返回值能够区分该方法调用是由于等待超时而返回还是由于其他线程执行了相应条件变量的 signal/signalAll 方法而返回。
 * Condition.awaitUntil(Date deadline)的唯一参数deadline表示等待的最后期限（ Deadline )，过了这个时间点就算等待超时。
 * Condition.awaitUntil(Date)返回值 true表示进行的等待尚未达到最后期限，
 *
 * 即此时方法的返回是由于其他线程执行了相应条件变量的 signal/signalAll 方法。
 * 由于Condition.await()/awaitUntil(Date)与Object.wait()类似，等待线程因执行Condition.awaitUntil(Date)而被暂停的同时，
 * 其持有的相应显式锁（即创建相应条件变量的显式锁)也会被释放3，等待线程被唤醒之后得以继续运行时需要再次申请相应的显式锁，
 * 然后等待线程对Condition.await()/awaitUntil(Date)的调用才能够返回。在等待线程被唤醒到其再次申请相应的显式锁的这段时间内，
 * 其他线程(或者通知线程本身)可能已经抢先获得相应的显式锁并在其临界区中更新了相关共享变量而使得等待线程所需的保护条件重新不成立。
 * 因此，Condition.awaitUntil(Date)返回true(等待未超时）的情况下我们可以选择继续等待,如清单5-3所示。
 *
 * 使用条件变量所产生的开销与wait/notify方法基本相似;
 * 不过由于条件变量的使用可以避免过早唤醒问题，因此其使用导致的上下文切换要比 wait/notify少一些。
 *
 * 2022/7/14
 */
public class TimeoutWaitWithCondition53 {

    private static final Lock lock = new ReentrantLock();

    private static final Condition condition = lock.newCondition();

    private static boolean ready = false;

    protected static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread() {
            @Override
            public void run() {
                for (;;) {
                    lock.lock();
                    try {
                        ready = random.nextInt(100) < 5 ? true : false;
                        if (ready) {
                            condition.signal();
                        }
                    } finally {
                        lock.unlock();
                    }

                    // 使当前线程暂停一段（随机）时间
                    Tools.randomPause(500);
                }// for循环结束
            }
        };
        t.setDaemon(true);
        t.start();
        waiter(1000);
    }

    public static void waiter(final long timeOut) throws InterruptedException {
        if (timeOut < 0) {
            throw new IllegalArgumentException();
        }

        // 计算等待的最后期限
        final Date deadline = new Date(System.currentTimeMillis() + timeOut);

        // 是否继续等待
        boolean continueToWait = true;

        lock.lock();
        try {
            while (!ready) {
                Debug.info("still not ready,continue to wait:%s", continueToWait);
                // 等待未超时，继续等待
                if (!continueToWait) {
                    // 等待超时退出
                    Debug.error("Wait timed out,unable to execution target action!");
                    return;
                }

                continueToWait = condition.awaitUntil(deadline);
            } // while循环结束

            // 执行目标动作
            guarededAction();
        } finally {
            lock.unlock();
        }
    }

    private static void guarededAction() {
        Debug.info("Take some action.");
        // ...
    }

}
