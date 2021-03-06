package cn.locusc.mtia.chapter5.codelist;

/**
 * @author Jay
 * 对传输通道的抽象
 *
 * 传输通道相当于如清单5-10所示的接口。
 * 在该接口中，类型参数Р代表产品的类型，take方法用于从传输通道中取出一个产品，put方法用于往传输通道中存入一个产品。
 * 显然，当传输通道为空的时候消费者无法取出一个产品，此时消费者线程可以进行等待，直到传输通道非空，即生产者线程生产了新的产品。
 * 当传输通道存储空间满的时候生产者无法往其中存入新的产品，此时生产者线程可以进行等待，直到传输通道非满，即有消费者消费了产品而腾出新的存储空间。
 * 生产者线程往传输通道中成功存入产品后就会唤醒等待传输通道非空的消费者线程,而消费者线程从传输通道中取出一个产品之后就会唤醒等待传输通道非满的生产者线程。
 * 我们称这种传输通道的运作方式为阻塞式( Blocking )，即从传输通道中存入一个产品或者取出一个产品时,
 * 相应的线程可能因为传输通道中没有产品或者其存储空间已满而被阻塞（暂停)。
 *
 * 术语定义
 * 一般而言，一个方法或者操作如果能够导致其执行线程被暂停（生命周期状态为WAITING或者 BLOCKED )，
 * 那么我们就称相应的方法/操作为阻塞方法（BlockingMethod )或者阻塞操作。
 * 可见，阻塞方法/操作能够导致上下文切换。常见的阻塞方法/操作包括InputStream.read()、ReentrantLock.lock()、申请内部锁等。
 * 相反，如果一个方法或者操作并不会导致其执行线程被暂停，那么相应的方法/操作就被称为非阻塞方法( Non-blocking Method)或者非阻塞操作。
 *
 * 提示 pageNum232
 * LinkedBlockingQueue适合在生产者线程和消费者线程之间的并发程度比较大的情况下使用。
 * ArrayBlockingQueue适合在生产者线程和消费者线程之间的并发程度较低的情况下使用。
 * SynchronousQueue适合在消费者处理能力与生产者处理能力相差不大的情况下使用。
 *
 * 2022/7/15
 */
public interface Channel510<P> {

    /**
     * 往传输通道中存入一个产品
     *
     * @param product
     *          产品
     */
    void put(P product) throws InterruptedException;

    /**
     * 从传输通道中取出一个产品
     *
     * @return 产品
     */
    P take() throws InterruptedException;

}
