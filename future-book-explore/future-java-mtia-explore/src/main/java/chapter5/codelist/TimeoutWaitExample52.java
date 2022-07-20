package chapter5.codelist;

import utils.Debug;
import utils.Tools;

import java.util.Random;

/**
 * @author Jay
 *
 * 使用Object.wait(long)实现等待超时控制
 *
 * Object.wait()的执行线程会一直处于WAITING状态,直到通知线程唤醒该线程并且保护条件成立。
 * 因此，Object.wait()所实现的等待是无限等待。Object.wait()方法还有个版本，其声明如下:
 *
 * public final void wait (long timeout) throws InterruptedException
 *
 * Object.wait(long)允许我们指定一个超时时间（单位为毫秒)。如果被暂停的等待线程在这个时间内没有被其他线程唤醒，
 * 那么、Java 虚拟机会自动唤醒该线程。不过Object.wait(long)既无返回值也不会抛出特定的异常，
 * 以便区分其返回是由于其他线程通知了当前线程还是由于等待超时。
 * 因此，使用Object.wait(long)的时候我们需要一些额外的处理，如清单5-2所示。
 *
 *
 * 在上述代码中，Object.wait(long)调用仍要放在一个循环语句之中。
 * 在每次调用object.wait(long)之前，我们总是先根据系统当前时间( now)和等待方法被调用的时间( start )计算出剩余的等待时间( waitTime),
 * 然后以该时间为参数去调用Object.wait(long)。并且，在执行目标动作前我们会再次判断保护条件( ready==true)是否成立，
 * 此时保护条件若仍然不成立，则说明循环语句中的Object.wait(long)的返回是由等待超时导致的。
 * Object.wait()调用相当于Object.wait(O)调用。
 *
 * 注意
 * Object.notify()唤醒的是其所属对象上的一个任意等待线程。
 * Object.notify()本身在唤醒线程时是不考虑保护条件的。
 * Object.notifyAll()方法唤醒的是其所属对象上的所有等待线程。
 * 使用Object.notify()替代 Object.notifyAll()时需要确保以下两个条件同时得以满足:
 *  一次通知仅需要唤醒至多一个线程。
 *  相应对象上的所有等待线程都是同质等待线程。
 *
 * 2022/7/14
 */
public class TimeoutWaitExample52 {

    private static final Object lock = new Object();

    private static boolean ready = false;

    protected static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(){
            @Override
            public void run() {
                for (;;) {
                    synchronized (lock) {
                        ready = random.nextInt(100) < 5 ? true : false;
                        if (ready) {
                            lock.notify();
                        }
                    }
                    // 使当前线程暂停一段（随机）时间
                    Tools.randomPause(500);
                } // for循环结束
            }
        };
        thread.setDaemon(true);
        thread.start();
        waiter(1000);
    }

    public static void waiter(final long timeOut) throws InterruptedException {
        if (timeOut < 0) {
            throw new IllegalArgumentException();
        }

        long start = System.currentTimeMillis();
        long waitTime;
        long now;

        synchronized (lock) {
            while (!ready) {
                now = System.currentTimeMillis();
                // 计算剩余等待时间
                waitTime = timeOut - (now - start);
                Debug.info("Remaining time to wait:%sms", waitTime);
                if (waitTime <= 0) {
                    // 等待超时退出
                    break;
                }
                lock.wait(waitTime);
            } // while循环结束

            if (ready) {
                // 执行目标动作
                guardedAction();
            } else {
                // 等待超时，保护条件未成立
                Debug.error("Wait timed out,unable to execution target action!");
            }
        } // 同步块结束

    }

    private static void guardedAction() {
        Debug.info("Take some action.");
        // ...
    }

}
