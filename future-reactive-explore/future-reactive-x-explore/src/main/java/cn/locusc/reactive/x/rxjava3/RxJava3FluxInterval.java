package cn.locusc.reactive.x.rxjava3;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RxJava3FluxInterval {

    /**
     * 基于时间的异步响应式流(rxjava3)
     */
    @Test
    public void test1() throws InterruptedException {
        // 创建基于时间的异步的响应式流
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe((s) -> {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println(s);
                });

        System.out.println(Thread.currentThread().getName());
        // 等待处理10s之后，关闭主线程，程序退出
        Thread.sleep(10000);
    }

    /**
     * 基于时间的异步响应式流 取消订阅
     */
    @Test
    public void test2() throws InterruptedException {
        // 使一个线程等待其他线程各自执行完毕后再执行.
        // 是通过一个计数器来实现的, 计数器的初始值是线程的数量.
        // 每当一个线程执行完毕后, 计数器的值就-1, 当计数器的值为0时,
        // 表示所有线程都执行完毕, 然后在闭锁上等待的线程就可以恢复工作了
        CountDownLatch latch = new CountDownLatch(1);

        // 返回订阅票据
        Disposable subscribe = Observable.interval(100, TimeUnit.MILLISECONDS)
                .subscribe(s -> {
                    System.out.println("响应流:" + Thread.currentThread().getName());
                    System.out.println(s);
                });

        System.out.println("主线程" + Thread.currentThread().getName());

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                // 等待3s
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 如果没有取消订阅, 则取消订阅
            if(!subscribe.isDisposed()) {
                subscribe.dispose();
            }
            latch.countDown();
        }).start();

        System.out.println("++++++++++++++++");
        // 主线程阻塞等待
        latch.await();
        System.out.println("----------------");
    }

}
