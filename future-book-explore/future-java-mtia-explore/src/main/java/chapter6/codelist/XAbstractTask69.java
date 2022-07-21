package chapter6.codelist;

import java.util.HashMap;

/**
 * @author Jay
 * 避免ThreadLocal可能导致的数据错乱
 *
 * 使用线程特有对象可能会导致如下几个问题。
 * 退化与数据错乱。由于线程和任务之间可以是一对多的关系，即一个线程可以先后执行多个任务，
 * 因此线程特有对象就相当于一个线程所执行的多个任务之间的共享对象。
 * 如果线程特有对象是个有状态对象且其状态会随着相应线程所执行的任务而改变，
 * 那么这个线程所执行的下一个任务可能"看到"来自前一个任务的数据，
 * 而这个数据可能与该任务并不匹配，从而导致数据错乱。
 * 因此，在一个线程可以执行多个任务的情况下（比如在生产者—消费者模式中）使用线程特有对象，
 * 我们需要确保每个任务的处理逻辑被执行前相应的线程特有对象的状态不受前一个被执行的任务影响。
 * 这通常可以通过在任务处理逻辑被执行前为线程局部变量重新关联一个线程特有对象（通过调用ThreadLocal.set(T)实现）或者重置线程特有对象的状态来实现。
 * 例如，清单6-9中的XAbstractTask子类的多个实例可以由一个线程负责执行（比如使用第5章的TaskRunner 来执行，代码参见清单5-14)，
 * 因此我们在preRun方法中将线程特有对象 HashMap 的内容清空，
 * 以避免前一个任务(XAbstractTask子类实例)执行时更改了线程特有对象的状态对当前执行的任务造成影响。
 * 从清单6-9中可以看出，在线程可以被重复使用来执行多个任务的情况下使用线程特有对象即使不会造成数据错乱，
 * 也可能导致这种线程特有对象实际上“退化”成为任务特有对象——被执行的任务可能更改了线程特有对象的状态，而这些状态一旦对其他任务可见又可能导致数据错乱，
 * 因此每个任务实际上需要的是状态会受该任务影响并且独立于其他任务的一个对象。
 *
 * 2022/7/18
 */
public abstract class XAbstractTask69 implements Runnable {

    static ThreadLocal<HashMap<String, String>> configHolder = new ThreadLocal<HashMap<String, String>>() {
        @Override
        protected HashMap<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    // 该方法总是会在任务处理逻辑被执行前执行
    protected void preRun() {
        // 清空线程特有对象HashMap实例，以保证每个任务执行前HashMap的内容是"干净"的
        configHolder.get().clear();
    }

    protected void postRun() {
        // 什么都不做
    }

    // 暴露给子类用于实现任务处理逻辑
    protected abstract void doRun();

    @Override
    public final void run() {
        try {
            preRun();
            doRun();
        } finally {
            postRun();
        }
    }

}
