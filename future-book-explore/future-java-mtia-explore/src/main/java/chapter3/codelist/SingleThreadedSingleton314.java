package chapter3.codelist;

/**
 * @author Jay
 * 单线程版单例模式的实现
 *
 * 单例模式所要实现的目标（效果）非常简单:保持一个类有且仅有一个实例"。
 * 出于性能的考虑，不少单例模式的实现会采用延迟加载( Lazy Loading)的方式，即仅在需要用到相应实例的时候才创建实例。
 * 从单线程应用程序的角度理解，采用延迟加载实现的一个单例模式如清单3-14所示。
 *
 * 严格来说，所谓“一个类有且仅有一个实例”隐含着一个前提——这个类是一个Java虚拟机实例(进程)中的一个Classs Loader所加载的类。
 * 这是考虑到了Java虚拟机的Class Loader机制:同一个类可以被多个Class Loader加载，
 * 这些Class Loader各自创建这个类的类实例(Class本身也是个对象)。因此，如果有多个Class Loader加载同一个类，
 * 那么所谓“单例”就无法满足——这些Class Loader各自的类实例都创建该类的唯一一个实例，
 * 实际上被创建的实例数就等于加载这个类的Class Loader的数量。
 *
 * 2022/7/4
 */
public class SingleThreadedSingleton314 {

    // 保存该类的唯一实例
    private static SingleThreadedSingleton314 instance = null;

    // 省略实例变量声明
    /*
     * 私有构造器使其他类无法直接通过new创建该类的实例
     */
    private SingleThreadedSingleton314() {
        // 什么也不做
    }

    /**
     * 创建并返回该类的唯一实例 <BR>
     * 即只有该方法被调用时该类的唯一实例才会被创建
     *
     * 在多线程环境下，getInstance()中的if语句形成一个check-then-act 操作，它不是一个原子操作。
     * 由于代码中未使用任何同步机制,因此该程序的运行可能出现线程交错的情形:在instance值还是null的时候，
     * 线程T和线程T同时执行到操作①。接着在T,执行操作②前T,已率先执行完操作②。
     * 下一时刻，当T执行到操作②的时候，尽管instance实际上已经不为null，但是T;此时依然会再创建一个实例（因为T执行操作①时instance为null )。
     * 这就导致了多个实例的创建，从而违背了初衷。当然，我们不难想到通过加锁可以解决这种问题，代码如清单3-15所示。
     */
    public static SingleThreadedSingleton314 getInstance() {
        if (null == instance) {// 操作①
            instance = new SingleThreadedSingleton314();// 操作②
        }
        return instance;
    }

    public void someService() {
        // 省略其他代码
    }

}
