package cn.locusc.reactive.x.rxjava1;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RxJava1FluxInterval {


    /**
     * 基于时间的异步响应式流(rxjava1)
     */
    @Test
    public void test1() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        // 返回订阅票据
        // 创建基于时间的异步响应流
        Subscription subscription = Observable.interval(100, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 如果订阅者没有取消订阅, 则取消订阅
            if (subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            latch.countDown();
        }).start();

        System.out.println("==============");
        latch.await();
        System.out.println("--------------");
    }


}
