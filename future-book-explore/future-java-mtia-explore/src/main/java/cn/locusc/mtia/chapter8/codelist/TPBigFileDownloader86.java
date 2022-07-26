package cn.locusc.mtia.chapter8.codelist;

import cn.locusc.mtia.chapter4.codelist.case01.BigFileDownloader41;
import cn.locusc.mtia.chapter4.codelist.case01.DownloadTask42;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Jay
 * 基于线程池的大文件下载器
 *
 * 线程是一种昂贵的资源,其开销主要包括以下几个方面。
 * 线程的创建与启动的开销。与普通的对象相比，Java线程还占用了额外的存储空间——栈空间。并且,线程的启动会产生相应的线程调度开销。
 * 线程的销毁。线程的销毁也有其开销。
 * 线程调度的开销。线程的调度会导致上下文切换，从而增加处理器资源的消耗，使得应用程序本身可以使用的处理器资源减少。
 * 一个系统能够创建的线程总是受限于该系统所拥有的处理器数目。无论是CPU密集型还是I/O密集型线程，这些线程的数量的临界值总是处理器的数目。
 *
 * 因此,从整个系统乃至整个主机的角度来看我们需要一种有效使用线程的方式。线程池就是有效使用线程的一种常见方式。
 *
 * 常见的对象池（比如数据库连接池）的实现方式是对象池(本身也是个对象）内部维护一定数量的对象，
 * 客户端代码需要一个对象的时候就向对象池申请（借用)一个对象，用完之后再将该对象返还给对象池,
 * 于是对象池中的一个对象就可以先后为多个客户端线程服务。线程池本身也是一个对象，不过它的实现方式与普通的对象池不同，
 * 如图8-2所示:线程池内部可以预先创建一定数量的工作者线程，客户端代码并不需要向线程池借用线程而是将其需要执行的任务作为一个对象提交给线程池,
 * 线程池可能将这些任务缓存在队列(工作队列)之中，而线程池内部的各个工作者线程则不断地从队列中取出任务并执行之。
 * 因此，线程池可以被看作基于生产者—消费者模式的一种服务，该服务内部维护的工作者线程相当于消费者线程，
 * 线程池的客户端线程相当于生产者线程,客户端代码提交给线程池的任务相当于“产品”，线程池内部用于缓存任务的队列相当于传输通道。
 *
 * java.util.concurrent.ThreadPoolExecutor类就是一个线程池,客户端代码可以调用ThreadPoolExecutor.submit方法向其提交任务，
 * ThreadPoolExecutor.submit方法声明如下:public Future<?> submit (Runnable task)
 *
 * 其中，task 参数是一个 Runnable实例，它代表客户端需要线程池代为执行的任务。为便于讨论，这里我们先忽略该方法的返回值。
 *
 * 线程池内部维护的工作者线程的数量就被称为该线程池的线程池大小( Pool Size )。
 * ThreadPoolExecutor的线程池大小有3种形态:当前线程池大小( Current Pool Size)表示线程池中实际工作者线程的数量;
 * 最大线程池大小(Maximum Pool Size)表示线程池中允许存在的工作者线程的数量上限，其具体取值可参考第4章的式(4-5 );
 * 核心线程大小( Core Pool Size）表示一个不大于最大线程池大小的工作者线程数量上限。它们之间的数量关系如下:
 * 当前线程池大小≤核心线程池大小≤最大线程池大小，或核心线程池大小≤当前线程池大小≤最大线程池大小
 *
 * 这里，除了当前线程池大小是对线程池中现有的工作者线程进行计数的结果,
 * 其他有关线程池大小的概念实际上都是由开发人员或者系统配置数据指定的一个阈值( Threshold )。这些阈值的具体含义下文会介绍。
 *
 * ThreadPoolExecutor 的构造器中包含参数数量最多的一个构造器的声明如下:
 * public ThreadPoolExecutor (int corePoolsize,
 *  int maximumPoolsize,
 *  long keepAliveTime,TimeUnit unit,
 *  BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory,
 *  RejectedExecutionHandler handler)
 *
 * 其中，workQueue是被称为工作队列的阻塞队列，它相当于生产者—消费者模式中的传输通道，corePoolSize 用于指定线程池核心大小，
 * maximumPoolSize用于指定最大线程池大小。keepAliveTime和 unit合在一起用于指定线程池中空闲( Idle )线程的最大存活时间。
 * threadFactory指定用于创建工作者线程的线程工厂。handler参数下面会介绍。
 *
 * 在初始状态下，客户端每提交一个任务线程池就创建一个工作者线程来处理该任务。
 * 随着客户端不断地提交任务，当前线程池大小也相应增加。在当前线程池大小达到核心线程池大小的时候，
 * 新来的任务会被存入工作队列之中。这些缓存的任务由线程池中的所有工作者线程负责取出进行执行。
 * 线程池将任务存入工作队列的时候调用的是BlockingQueue的非阻塞方法 offer(E e)，因此工作队列满并不会使提交任务的客户端线程暂停。
 * 当工作队列满的时候，线程池会继续创建新的工作者线程，直到当前线程池大小达到最大线程池大小。
 * 线程池是通过调用threadFactory.newThread方法来创建工作者线程的。
 * 如果我们在创建线程池的时候没有指定线程工厂(即调用了ThreadPoolExecutor 的其他构造器)，
 * 那么ThreadPoolExecutor 会使用Executors.defaultThreadFactory()所返回的默认线程工厂。
 * 当线程池饱和( Saturated )时，即工作者队列满并且当前线程池大小达到最大线程池大小的情况下﹐客户端试图提交的任务会被拒绝(Reject )。
 * 为了提高线程池的可靠性,Java标准库引入了一个RejectedExecutionHandler接口用于封装被拒绝的任务的处理策略，该接口仅定义了如下方法:
 *
 * void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
 *
 * 其中,r代表被拒绝的任务，executor代表拒绝任务r的线程池实例。
 * 我们可以通过线程池的构造器参数handler或者线程池的 setRejectedExecutionHandler(RejectedExecutionHandlerhandler)
 * 方法来为线程池关联一个RejectedExecutionHandler。当客户端提交的任务被拒绝时,
 * 线程池所关联的RejectedExecutionHandler的 rejectedExecution方法会被线程池调用。
 * ThreadPoolExecutor自身提供了几个现成的RejectedExecutionHandler接口实现类（见表8-1)，
 * 其中 ThreadPoolExecutor.AbortPolicy是 ThreadPoolExecutor使用的默认RejectedExecutionHandler。
 * 如果默认的 RejectedExecutionHandler(它会直接抛出异常）无法满足要求，
 * 那么我们可以优先考虑ThreadPoolExecutor自身提供的其他RejectedExecutionHandler，其次才去考虑使用自行实现的RejectedExecutionHandler接口。
 *
 * 在当前线程池大小超过线程池核心大小的时候,
 * 超过线程池核心大小部分的工作者线程空闲(即工作者队列中没有待处理的任务)时间达到keepAliveTime所指定的时间后就会被清理掉,
 * 即这些工作者线程会自动终止并被从线程池中移除。这种空闲线程清理机制有利于节约有限的线程资源，
 * 但是keepAliveTime值设置不合理(特别是设置得太小)可能导致工作者线程频繁地被清理和创建反而增加了开销!
 *
 * 线程池中数量上等于核心线程池大小的那部分工作者线程，习惯上我们称之为核心线程( Core Thread )。
 * 如前文所述，当前线程池大小是随着线程池接收到的任务的数量而逐渐向核心线程池大小靠拢的，
 * 即核心线程是逐渐被创建与启动的。
 * ThreadPoolExecutor.prestartAllCoreThreads()则使得我们可以使线程池在未接收到任何任务的情况下预先创建并启动所有核心线程,
 * 这样可以减少任务被线程池处理时所需的等待时间(等待核心线程的创建与启动)。
 *
 * ThreadPoolExecutor.shutdown()/shutdownNow()方法可用来关闭线程池。
 * 使用shutdown()关闭线程池的时候，已提交的任务会被继续执行，而新提交的任务会像线程池饱和时那样被拒绝掉。
 * ThreadPoolExecutor.shutdown()返回的时候线程池可能尚未关闭，即线程池中可能还有工作者线程正在执行任务。
 * 应用代码可以通过调用ThreadPoolExecutor.awaitTermination(long timeout,TimeUnit unit)来等待线程池关闭结束。
 * 使用ThreadPoolExecutor.shutdownNow()关闭线程池的时候，正在执行的任务会被停止，已提交而等待执行的任务也不会被执行。
 * 该方法的返回值是已提交而未被执行的任务列表，这为被取消的任务的重试提供了一个机会。
 * 由于ThreadPoolExecutor.shutdownNow()内部是通过调用工作者线程的interrupt方法来停止正在执行的任务的,
 * 因此某些无法响应中断的任务可能永远也不会停止。反过来说，在关闭线程池的时候如果我们能够确保已经提交的任务都已执行完毕并且没有新的任务会被提交，
 * 那么调用ThreadPoolExecutor.shutdownNow()总是安全可靠的。
 *
 * 在第4章第1个实战案例(大文件下载器)中,我们为每个下载子任务( DownloadTask实例)都创建一个相应的工作者线程，
 * 虽然这样做也能够大幅提高下载效率，但是线程资源的利用可能并不高。另外，一个下载子任务执行失败意味着整个大文件下载的失败，
 * 因此一个工作者线程抛出异常的时候(为了简单起见，我们不对异常进行重试处理)其他工作者线程也就没有必要再运行下去而是要提前终止。
 * 为解决上述两个问题,我们可以使用ThreadPoolExecutor来改写BigFileDownloader类(参见清单4-1)的dispatchWork方法和doCleanup方法（如清单8-6所示)。
 *
 * 这里，我们以实例变量executor的形式创建了一个线程池，用于负责文件下载子任务( DownloadTask实例)的执行。
 * 该线程池的核心线程池大小为2。考虑到该线程池的核心任务属于IO密集型任务（参见第4章)，因此我们将最大线程池大小设置为系统处理器数目的两倍。
 * 并且，我们使用ThreadPoolExecutor.CallerRunsPolicy 作为线程池饱和处理策略，这意味着如果有下载子任务因线程池饱和而被拒绝，
 * 那么这些子任务将由dispatchWork方法的执行线程（即main线程)来执行，从而确保了程序的可靠性。
 * dispatchWork方法会为每个 DownloadTask实例都创建一个包装任务，并在该包装中实现子任务下载异常处理逻辑，
 * 即在任何一个下载子任务处理失败的情况下都取消整个文件的下载。然后，dispatchWork方法将这个包装任务提交给线程池executor 执行。
 * 在整个文件下载完毕（即下载进度为100%)后doCleanup方法会被执行(由 BigFileDownloader.download(int, long)调用)。
 * 由于doCleanup方法被执行的时候所有下载子任务都已经执行结束并且不会有新的子任务被提交，
 * 因此在doCleanup方法中我们可以调用executor.shutdownNow()来安全、可靠地将线程池关闭。
 * 通过对比上述程序与清单4-1中的程序的运行，我们可以发现使用线程池的方案与清单4-1所采用的直接使用工作者线程的方案相比在文件下载速率方面差别不大，
 * 但是前者所使用的线程数量要少得多，即提高了线程资源利用率。
 *
 * 从本案例可以看出，由于线程池（消费者）通常需要接收来自不同客户端（生产者)线程所提交的任务，
 * 因此一般情况下我们会以实例变量（或者静态变量）的形式来存储ThreadPoolExecutor实例。
 *
 * 2022/7/20
 */
public class TPBigFileDownloader86 extends BigFileDownloader41 {

    final static int N_CPU = Runtime.getRuntime().availableProcessors();
    final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, N_CPU * 2, 4,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(N_CPU * 8),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public TPBigFileDownloader86(String file) throws Exception {
        super(file);
    }

    public static void main(String[] args) throws Exception {
        final int argc = args.length;
        TPBigFileDownloader86 downloader = new TPBigFileDownloader86(args[0]);
        long reportInterval = argc >= 2 ? Integer.valueOf(args[1]) : 10;

        // 平均每个处理器执行8个下载子任务
        final int taskCount = N_CPU * 8;
        downloader.download(taskCount, reportInterval * 1000);
    }

    @Override
    protected void dispatchWork(final DownloadTask42 dt, int workerIndex) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    dt.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 任何一个下载子任务出现异常就取消整个下载任务
                    cancelDownload();
                }
            }
        });
    }

    @Override
    protected void doCleanup() {
        executor.shutdownNow();
        super.doCleanup();
    }

}
