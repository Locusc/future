package chapter5.codelist;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Jay
 * 通用任务执行器
 *
 * 5.5.6再探线程与任务之间的关系
 * 在生产者—消费者模式中，一个产品也可以代表消费者线程需要执行的任务。即使是在单生产者—单消费者模式中一个生产者线程也可以生产多个产品（任务)，
 * 而这些产品所代表的任务都是由一个消费者线程负责执行（消费)的。因此，线程和任务之间可以是一对多的关系，即一个线程可以先后执行多个任务。
 * 从这点来看，生产者—消费者模式有利于充分利用有限的线程资源:一个线程可以执行多个而不是一个任务。例如，清单5-14展示了一个通用的任务执行器TaskRunner。
 * TaskRunner 的实例变量channel相当于传输通道，TaskRunner 内部维护的工作者线程相当于消费者线程。
 * TaskRunner.submit(Runnable)的执行线程相当于生产者线程。生产者只需要调用TaskRunner.submit(Runnable)提交一个任务(Runnable接口实例，相当于产品)，
 * 该任务即可以被TaskRunner执行。显然，一个TaskRunner实例（对应一个工作者线程)可以用于执行生产者提交的多个任务。
 * 2022/7/15
 */
public class TaskRunner514 {

    protected final BlockingQueue<Runnable> channel;

    protected volatile Thread workerThread;

    public TaskRunner514(BlockingQueue<Runnable> channel) {
        this.channel = channel;
        this.workerThread = new WorkerThread();
    }

    public TaskRunner514() {
        this(new LinkedBlockingQueue<>());
    }

    public void init() {
        final Thread thread = workerThread;
        if (null != thread) {
            thread.start();
        }
    }

    public void submit(Runnable task) throws InterruptedException {
        channel.put(task);
    }

    class WorkerThread extends Thread {
        @Override
        public void run() {
            Runnable task = null;
            // 注意：下面这种代码写法实际上可能导致工作者线程永远无法终止！
            // “5.6 对不起，打扰一下：线程中断机制”中我们将会解决这个问题。
            try {
                for (;;) {
                    task = channel.take();
                    try {
                        task.run();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }// for循环结束
            } catch (InterruptedException e) {
                // 什么也不做
            }
        }// run方法结束
    }// WorkerThread结束

}
