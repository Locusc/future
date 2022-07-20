package chapter5.codelist;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @author Jay
 * 基于Semaphore的支持流量控制的传输通道实现。
 * Semaphore.acquire()和 Semaphore.release()总是配对使用。
 * 应用代码在访问虚拟资源前调用Semaphore.acquire()来申请配额，并在虚拟资源访问结束后调用Semaphore.release()来返回配额。
 * 由于Semaphore本身并不强制这种配对，即一个线程可以在未执行 Semaphore.acquire()的情况下执行Semaphore.release()，
 * 因此Semaphore.acquire()/release()的配对使用需要由应用代码来保证。
 * 这点和锁的获得与释放有所不同，因为一个线程只有在持有某个锁的情况下才能够释放该锁。
 *
 * Semaphore.release()调用总是应该放在一个finally块中,以避免虚拟资源访问出现异常的情况下当前线程所获得的配额无法返还（类似于锁泄漏）。
 *
 * 注意
 * Semaphore.acquire()和 Semaphore.release()总是配对使用的，这点需要由应用代码自身来保证。
 * Semaphore.release()调用总是应该放在一个finally块中,以避免虚拟资源访问出现异常的情况下当前线程所获得的配额无法返还。
 *
 * 创建Semaphore实例时如果构造器中的参数permits值为1，那么所创建的Semaphore实例相当于一个互斥锁。
 * 与其他互斥锁不同的是，由于一个线程可以在未执行过Semaphore.acquire()的情况下执行相应的Semaphore.release()，
 * 因此这种互斥锁允许一个线程释放另外一个线程锁所持有的锁。
 *
 * 配额本身可被看作程序执行特定操作前所需持有的资源，因此对配额的调度也涉及公平性问题。
 * 默认情况下，Semaphore采用的是非公平性调度策略，因此在可用配额数为О的情况下，
 * 一个线程返回一个配额之后获得配额的那个线程可能是等待队列中那个被唤醒的线程，也可能是其他申请配额的活跃线程。
 *
 * <P> "产品类型"
 * 2022/7/15
 */
public class SemaphoreBasedChannel511<P> implements Channel510<P> {

    private final BlockingQueue<P> queue;

    private final Semaphore semaphore;

    /**
     * @param queue
     *          阻塞队列，通常是一个无界阻塞队列。
     * @param flowLimit
     *          流量限制数
     */
    public SemaphoreBasedChannel511(BlockingQueue<P> queue, int flowLimit) {
        this(queue, flowLimit, false);
    }

    public SemaphoreBasedChannel511(BlockingQueue<P> queue, int flowLimit,
                                 boolean isFair) {
        this.queue = queue;
        this.semaphore = new Semaphore(flowLimit, isFair);
    }

    @Override
    public void put(P product) throws InterruptedException {
        semaphore.acquire(); // 申请一个配额
        try {
            queue.put(product); // 访问虚拟资源
        } finally {
            semaphore.release(); // 返回一个配额
        }
    }

    @Override
    public P take() throws InterruptedException {
        return queue.take();
    }

}
