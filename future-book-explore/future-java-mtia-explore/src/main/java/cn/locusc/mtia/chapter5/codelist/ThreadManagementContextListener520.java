package cn.locusc.mtia.chapter5.codelist;

import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jay
 * 在web应用中实现线程停止
 * Java Web 应用中应用代码自身所启动的线程，
 * 比如在ServletContextListener.contextInitialized(ServletContextEvent)或者Servlet.init()中启动的线程，
 * 在该Web应用停止的时候如果仍在运行的，那么该Web 应用停止后这些线程（即使是守护线程）也可能仍然在运行。
 * 这是因为Web应用被停止的时候其所在的Web服务器（容器）仍然在运行，即相应的Java 进程仍然还在，
 * 所以该进程中启动的线程如果没有被主动停止，那么它可能还在运行。这些线程（对象)无法被垃圾回收就会导致它们所引用的对象也无法被垃圾回收，从而可能导致内存泄漏!
 *
 *
 * 某些Web 服务器考虑到了这一点并对此做了一些补救的措施。
 * 例如，Tomcat 6.0.37会在Web应用停止的时候检测是否存在由Web应用自身启动且未结束的线程，
 * 如果有这样的线程，那么Tomcat 会尽其最大努力来将这些线程停止。
 * 尽管如此，Tomcat还是无法保证这些线程能够完全被停止，即使能够停止也无法保证这种停止是优雅的。因此，我们不能依赖Web服务器，
 * 而是要在应用停止时自行将这些线程停止。为此，我们可以维护一个线程终止登记表ThreadTerminationRegistry，
 * 用于记录哪些线程是需要在Web应用停止时被主动停止的，如清单5-19所示。
 *
 * 以Tomcat 6.0.37为例，Tomcat最终会调用Thread.stop()这个被废弃的方法来强行停止这些线程。而
 * Thread.stop()不一定就能够将目标线程停止，并且它无法以优雅的方式停止线程。
 *
 *
 * 应用程序每创建一个(或者多个)不会自动终止的工作者线程（这类线程的run方法
 * 体通常是一个循环语句）时，就调用ThreadTerminationRegistry.register(Handler)来登记一个线程终止处理器( ThreadTerminationRegistry.Handler实例)，如清单5-20中的语句③所示。
 * 线程终止处理器的 terminate方法封装了相应线程（一个或者多个）的终止逻辑，如清单5-20中的语句②所示。
 * 当Web应用停止的时候,我们就通过ThreadTerminationRegistry.clearThreads()调用主动将所有登记过的线程停止，如清单5-20中的语句①所示。
 *
 * 2022/7/15
 */
//@WebListener
public class ThreadManagementContextListener520 implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 停止所有登记的线程
        ThreadTerminationRegistry519.INSTANCE.clearThreads();// 语句①
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 创建并启动一个数据库监控线程
        AbstractMonitorThread databaseMonitorThread;
        databaseMonitorThread = new AbstractMonitorThread(2000) {
            @Override
            protected void doMonitor() {
                Debug.info("Monitoring database...");
                // ...

                // 模拟实际的时间消耗
                Tools.randomPause(100);
            }
        };

        databaseMonitorThread.doMonitor();
    }

    /**
     * 抽象监控线程
     */
    static abstract class AbstractMonitorThread extends Thread {
        // 监控周期
        private final long interval;
        // 线程停止标记
        final AtomicBoolean terminationToken = new AtomicBoolean(false);

        public AbstractMonitorThread(long interval) {
            // 设置为守护线程!
            this.interval = interval;
            // 设置为守护线程
            setDaemon(true);

            ThreadTerminationRegistry519.Handler handler;

            handler = new ThreadTerminationRegistry519.Handler() {
                @Override
                public void terminate() {
                    terminationToken.set(true);
                    AbstractMonitorThread.this.interrupt();
                }
            };  // 语句②

            ThreadTerminationRegistry519.INSTANCE.register(handler); // 语句③
        }

        @Override
        public void run() {
            try {
                while (!terminationToken.get()) {
                    doMonitor();
                    Thread.sleep(interval);
                }
            } catch (InterruptedException e) {
                // 什么也不做
            }
        }

        // 子类覆盖该方法来实现监控逻辑
        protected abstract void doMonitor();
    }

}
