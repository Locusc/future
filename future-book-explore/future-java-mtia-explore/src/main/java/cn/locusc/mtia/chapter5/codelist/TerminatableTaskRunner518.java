package cn.locusc.mtia.chapter5.codelist;

import cn.locusc.mtia.utils.Debug;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jay
 * 通用的线程优雅停止办法实现
 *
 * 某些情况下，我们可能需要主动停止线程而不是等待线程自然终止( run方法返回)。一些典型场景如下。
 * 1.服务或者系统关闭。当一个服务不再被需要的时候，我们应该及时停止该服务所启动的工作者线程以节约宝贵的线程资源。
 * 由于非守护线程（用户线程）会阻止Java 虚拟机正常关闭，因此在系统停止前所有用户线程都应该先行停止。
 * 2.错误处理。同质（线程的任务处理逻辑相同）工作者线程中的一个线程出现不可恢复的异常时，
 * 其他线程往往就没有必要继续运行下去了，此时我们需要主动停止其他工作者线程。
 * 例如，第4章的第1个实战案例（大文件下载）中的一个下载线程如果出现了不可恢复的异常，那么其他下载线程即使运行到自然终止，
 * 最终整个大文件下载也还是失败的（文件不完整），这时我们就需要将其他下载线程主动停止掉。
 * 3.用户取消任务。在某些比较耗时的任务执行过程中用户可能会取消这个任务，这时任务的取消往往是通过主动停止相应的工作者线程实现的。
 *
 *
 * 我们不难想到主动停止一个线程的实现思路:为待停止的线程（目标线程)设置一个线程停止标记（布尔型数据)，
 * 目标线程检测到该标志值为true时则设法让其 run方法返回，这样就实现了线程的终止。依照这个思路，
 * 乍一看似乎线程中断标记可以作为线程停止标记，而目标线程则可以通过响应中断来实现其停止,
 * 但是由于线程中断标记可能会被目标线程所执行的某些方法清空,因此从通用性的角度来看线程中断标记并不能作为线程停止标记!
 * 例如，上文的通用任务执行器TaskRunner(参见清单5-14）中维护的工作者workerThread看起来似乎是可以通过workerThread.interrupt()调用来停止的
 * ——因为workerThread.run()对channel.take() ( BlockingQueue.take())的调用可能由于其他线程调用workerThread.interrupt()
 * 而抛出InterruptedException(响应中断)，并且 workerThread对该异常的处理方式是捕获并在捕获后使其run方法返回，如下代码片段所示。
 *
 * 但是，光使用专门的实例变量来作为线程停止标记仍然不够，这是由于当线程停止标记置为 true(表示目标线程需要被停止)的时候，
 * 目标线程可能因为执行了一些阻塞方法(比如CountDownLatch.await())而被暂停，因此，这时线程停止标记压根儿不会对目标线程产生任何影响!
 * 由此可见，为了使线程停止标记的设置能够起作用，我们可能还需要给目标线程发送中断以将其唤醒，使之得以判断线程停止标记。
 *
 * 另外，在生产者—消费者模式中一个线程试图停止目标线程的时候，该线程可能仍然有尚未处理完毕的任务，
 * 因此我们可能需要以“优雅”的方式将该线程停止——目标线程只有在其处理完所有待处理任务之后才能够终止。
 *
 * 综上所述，一个比较通用且能够以优雅的方式实现线程停止的方案如清单5-18所示。
 *
 * 这里，我们使用布尔型变量inUse作为线程停止标记，使用原子变量reservations表示目标线程待处理任务的数量（即传输通道中任务的数量)。
 * submit方法每接收到一个提交的任务时便将reservations的值增加1(语句①)。
 * 在 shutdown方法中，我们在将inUse置为 false(语句②)的时候还向目标线程发送中断（语句③)。
 * 接着，我们使目标线程的run方法每次从传输通道中取出一个任务前判断线程停止标记和待处理任务的数量（语句④)。
 * 若此时客户端不会再提交新的任务( inUse==false)且无待处理任务（ reservations.get()≤0 )，那么目标线程就可以优雅终止了;
 * 否则，目标线程从传输通道中取出一个任务执行后，会将待处理任务数减1(语句⑤)。
 * 目标线程的run方法还对InterruptedException进行了捕获，并在捕获到该异常后使其返回(线程随之终止)。
 * 这里，run方法所捕获的异常只可能是channel.take()调用所抛出的。由于我们不仅仅对中断进行了处理,
 * 还在每次取出待处理任务前判断了线程停止标记，因此，即使是客户端代码在调用shutdown方法那一刻，
 * 目标线程正在执行task.run()且 task.run()中的代码清空了线程中断标记，
 * 而使得后续执行的channel.take()调用无法抛出InterruptedException(因为线程中断标记被task.run()
 * 中的代码清空了，如清单5-17所示）的情况下、目标线程也还有退路——它能够通过对线程停止标记的判断而实现停止。
 *
 * 2022/7/15
 */
public class TerminatableTaskRunner518 implements TaskRunnerSpec {

    protected final BlockingQueue<Runnable> channel;
    // 线程停止标记
    protected volatile boolean inUse = true;
    // 待处理任务计数器
    public final AtomicInteger reservations = new AtomicInteger(0);

    private volatile Thread workerThread;

    public TerminatableTaskRunner518(BlockingQueue<Runnable> channel) {
        this.channel = channel;
        this.workerThread = new WorkerThread();
    }

    public TerminatableTaskRunner518() {
        this(new LinkedBlockingQueue<>());
    }

    @Override
    public void init() {
        final Thread t = workerThread;
        if (null != t) {
            t.start();
        }
    }

    @Override
    public void submit(Runnable task) throws InterruptedException {
        channel.put(task);
        reservations.incrementAndGet();
    }

    public void shutdown() {
        Debug.info("Shutting down service...");
        inUse = false;// 语句①
        final Thread t = workerThread;
        if (null != t) {
            t.interrupt();// 语句②
        }
    }

    public void cancelTask() {
        Debug.info("Canceling in progress task...");
        workerThread.interrupt();
    }

    class WorkerThread extends Thread {

        @Override
        public void run() {
            Runnable task = null;
            try {
                for (;;) {
                    // 线程不再被需要，且无待处理任务
                    if (!inUse && reservations.get() <= 0) {// 语句③
                        break;
                    }
                    task = channel.take();
                    try {
                        task.run();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    // 使待处理任务数减少1
                    reservations.decrementAndGet();// 语句④
                } // for循环结束
            } catch (InterruptedException e) {
                Debug.info(e.getMessage());
                workerThread = null;
            }
            Debug.info("worker thread terminated.");
        }// run方法结束
    }// WorkerThread结束

}
