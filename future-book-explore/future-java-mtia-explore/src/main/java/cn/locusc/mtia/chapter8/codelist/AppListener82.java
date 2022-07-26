package cn.locusc.mtia.chapter8.codelist;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jay
 * 在web应用中使用默认UncaughtExceptionHandler
 *
 * 线程组本身也实现了UncaughtExceptionHandler接口。如果一个线程没有关联的UncaughtExceptionHandler实例，
 * 那么该线程异常终止前其所属线程组的uncaughtException方法会被调用。
 * 线程组的uncaughtException方法会调用其父线程组的uncaughtException方法并传递同样的两个参数( t和 e )。
 * 如果一个线程组没有其父线程组3,那么线程组的uncaughtException方法会调用默认UncaughtExceptionHandler的uncaughtException
 * 方法来处理线程的异常终止。默认UncaughtExceptionHandler适用于所有线程，
 * 即任何一个线程异常终止时默认UncaughtExceptionHandler都有可能会被调用。
 * Thread.setDefaultUncaughtExceptionHandler方法可用来指定默认UncaughtExceptionHandler。
 * 针对一个线程的异常终止，该线程所关联的UncaughtExceptionHandler实例、
 * 该线程所在的线程组以及默认 UncaughtExceptionHandler 之中只有一个UncaughtExceptionHandler实例会被选中。
 * UncaughtExceptionHandler 实例的选择优先级如图8-1所示。
 *
 * 2022/7/20
 */
public class AppListener82 implements ServletContextListener {

    final static Logger LOGGER = Logger.getAnonymousLogger();

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        // 设置默认UncaughtExceptionHandler
        Thread.UncaughtExceptionHandler ueh = new LoggingUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(ueh);

        // 启动若干工作者线程
        startServices();
    }

    static class LoggingUncaughtExceptionHandler implements
            Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            String threadInfo = "Thread[" + t.getName() + "," + t.getId() + ","
                    + t.getThreadGroup().getName() + ",@" + t.hashCode() + "]";

            // 将线程异常终止的相关信息记录到日志中
            LOGGER.log(Level.SEVERE, threadInfo + " terminated:", e);
        }
    }

    protected void startServices() {
        // 省略其他代码
    }

    protected void stopServices() {
        // 省略其他代码
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        Thread.setDefaultUncaughtExceptionHandler(null);
        stopServices();
    }

}
