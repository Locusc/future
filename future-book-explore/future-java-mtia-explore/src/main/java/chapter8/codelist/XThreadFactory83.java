package chapter8.codelist;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jay
 * 使用Thread Factory创建线程
 *
 * 从JDK 1.5开始，Java标准库本身就支持创建线程的工厂方法(Factory Method ) 3。
 * ThreadFactory接口是工厂方法模式的一个实例，它定义了如下工厂方法:
 * public Thread newThread(Runnable r)
 * newThread方法可以用来创建线程，该方法的参数r代表所创建的线程需要执行的任务。
 * 如果把线程对象看作某种“产品”，那么通过new方式创建线程就好比手工制作，而使用ThreadFactory接口创建线程则好比是工厂采用标准化的流水线进行生产。
 * 我们可以在ThreadFactory.newThread方法中封装线程创建的逻辑,这使得我们能够以统一的方式为线程的创建、配置做一些非常有用的动作。
 * 在如清单8-3所示的例子中, ThreadFactory实现类XThreadFactory 的 newThread方法为其创建的每一个线程做了这样一些列的处理逻辑:
 * 为线程关联UncaughtExceptionHandler，为线程设置一个含义更加具体的有助于问题定位的名称，确保线程是一个用户线程,
 * 确保线程的优先级为正常级别，以及在线程创建的时候打印相关日志信息。
 * 并且，这些线程的toString()返回值更加有利于问题的定位——在对真实的(商用)多线程系统中的问题进行定位的过程中，
 * 将一个线程与另外一个线程区分开来非常有助于问题的定位，线程ID以及线程对象的身份标识(Hash Code )是将一个线程与另外一个线程区分开来的重要依据，
 * 而Thread.toString()的返回值并没有体现这一点。可见，XThreadFactory不仅仅是为我们提供了一个新的线程，
 * 它还为这个线程做了一些有利于简化客户端代码以及有利于代码调试和问题定位的动作。
 *
 * 2022/7/20
 */
public class XThreadFactory83 implements ThreadFactory {

    final static Logger LOGGER = Logger.getAnonymousLogger();

    private final UncaughtExceptionHandler ueh;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    // 所创建的线程的线程名前缀
    private final String namePrefix;

    public XThreadFactory83(UncaughtExceptionHandler ueh, String name) {
        this.ueh = ueh;
        this.namePrefix = name;
    }

    public XThreadFactory83(String name) {
        this(new LoggingUncaughtExceptionHandler(), name);
    }

    public XThreadFactory83(UncaughtExceptionHandler ueh) {
        this(ueh, "thread");
    }

    public XThreadFactory83() {
        this(new LoggingUncaughtExceptionHandler(), "thread");
    }

    protected Thread doMakeThread(final Runnable r) {
        return new Thread(r) {
            @Override
            public String toString() {
                // 返回对问题定位更加有益的信息
                ThreadGroup group = getThreadGroup();
                String groupName = null == group ? "" : group.getName();
                String threadInfo = getClass().getSimpleName() + "[" + getName() + ","
                        + getId() + ","
                        + groupName + "]@" + hashCode();
                return threadInfo;
            }
        };
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = doMakeThread(r);
        t.setUncaughtExceptionHandler(ueh);
        t.setName(namePrefix + "-" + threadNumber.getAndIncrement());
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("new thread created" + t);
        }
        return t;
    }

    static class LoggingUncaughtExceptionHandler implements
            UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // 将线程异常终止的相关信息记录到日志中
            LOGGER.log(Level.SEVERE, t + " terminated:", e);
        }
    }// LoggingUncaughtExceptionHandler类定义结束

}
