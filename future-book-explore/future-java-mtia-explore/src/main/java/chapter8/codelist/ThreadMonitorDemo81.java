package chapter8.codelist;

import utils.Debug;
import utils.Tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jay
 * 使用UncaughtExceptionHandler实现线程监控
 *
 * 如果线程的run方法抛出未被捕获的异常（ Uncaught Exception )，
 * 那么随着run方法的退出，相应的线程也提前终止。对于线程的这种异常终止，我们如何得知并做出可能的补救动作，
 * 例如重新创建并启动一个替代线程呢?JDK 1.5为了解决这个问题引入了UncaughtExceptionHandler接口。
 * 该接口是在Thread类内部定义的,它只定义了一个方法:
 * void uncaughtException (Thread t, Throwable e)
 *
 * uncaughtException方法中的两个参数包括了异常终止的线程本身（对应第1个参数)以及导致线程提前终止的异常（对应第2个参数)。
 * 那么，在uncaughtException方法当中我们就可以做一些有意义的事情，比如将线程异常终止的相关信息记录到日志文件中，
 * 甚至于为异常终止的线程创建并启动一个替代线程。设thread为任意一个线程，eh为任意一个UncaughtExceptionHandler 实例，
 * 那么我们可以在启动thread前通过调用thread.setUncaughtExceptionHandler(ch)来为 thread 关联一个UncaughtExceptionHandler。
 * 当thread抛出未被捕获的异常后thread.run()返回，接着thread 会在其终止前调用ch.uncaughtException方法。
 *
 * 清单8-1展示了一个利用UncaughtExceptionHandler 实现线程监控的例子。在这个例子中，
 * 系统的某个重要服务( ThreadMonitorDemo )内部维护了一个工作者线程( WorkerThread )用于实现该服务的核心功能。
 * 因此，一旦这个工作者线程由于某些未捕获的异常(比如NullPointerException)而提前终止,那么我们需要在第一时间得到“通知”,
 * 并为该线程创建并启动一个替代线程来接替其完成其任务，以保障该服务的可靠性。这个接替的过程就是通过UncaughtExceptionHandler实现的:
 * ThreadMonitor.uncaughtException方法会重新将工作者线程的启动标记init置为false，并再次调用init方法来创建并启动一个新的工作者线程,
 * 用于接替异常中止的工作者线程。
 *
 * 可见，工作者线程( WorkerThread )中途的确异常终止过，但是由于我们在侦测到该线程异常终止的时候创建了相应的替代线程，
 * 因此该线程的异常终止并没有影响ThreadMonitorDemo继续对外提供服务，从而使ThreadMonitorDemo的可靠性得以保障。
 * 另外，从上述输出中可以看出，UncaughtExceptionHandler.uncaughtException方法是执行在抛出异常e的线程t之中的，
 * 在执行UncaughtExceptionHandler.uncaughtException方法的时候线程t还是存活的( Live )，
 * UncaughtExceptionHandler.uncaughtException方法返回之后线程t就终止了。
 *
 * 2022/7/20
 */
public class ThreadMonitorDemo81 {

    volatile boolean inited = false;
    static int threadIndex = 0;
    final static Logger LOGGER = Logger.getAnonymousLogger();
    final BlockingQueue<String> channel = new ArrayBlockingQueue<String>(100);

    public static void main(String[] args) throws InterruptedException {
        ThreadMonitorDemo81 demo = new ThreadMonitorDemo81();
        demo.init();
        for (int i = 0; i < 100; i++) {
            demo.service("test-" + i);
        }

        Thread.sleep(2000);
        System.exit(0);
    }

    public synchronized void init() {
        if (inited) {
            return;
        }
        Debug.info("init...");
        WokrerThread t = new WokrerThread();
        t.setName("Worker0-" + threadIndex++);
        // 为线程t关联一个UncaughtExceptionHandler
        t.setUncaughtExceptionHandler(new ThreadMonitor());
        t.start();
        inited = true;
    }

    public void service(String message) throws InterruptedException {
        channel.put(message);
    }

    private class ThreadMonitor implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Debug.info("Current thread is `t`:%s, it is still alive:%s",
                    Thread.currentThread() == t, t.isAlive());

            // 将线程异常终止的相关信息记录到日志中
            String threadInfo = t.getName();
            LOGGER.log(Level.SEVERE, threadInfo + " terminated:", e);

            // 创建并启动替代线程
            LOGGER.info("About to restart " + threadInfo);
            // 重置线程启动标记
            inited = false;
            init();
        }

    }// 类ThreadMonitor定义结束

    private class WokrerThread extends Thread {
        @Override
        public void run() {
            Debug.info("Do something important...");
            String msg;
            try {
                for (;;) {
                    msg = channel.take();
                    process(msg);
                }
            } catch (InterruptedException e) {
                // 什么也不做
            }
        }

        private void process(String message) {
            Debug.info(message);
            // 模拟随机性异常
            int i = (int) (Math.random() * 100);
            if (i < 2) {
                throw new RuntimeException("test");
            }
            Tools.randomPause(100);
        }
    }// 类ThreadMonitorDemo定义结束

}
