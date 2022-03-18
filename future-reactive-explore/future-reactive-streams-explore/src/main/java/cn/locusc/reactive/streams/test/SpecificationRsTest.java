package cn.locusc.reactive.streams.test;

import cn.locusc.reactive.streams.specification.AsyncIterablePublisher;
import cn.locusc.reactive.streams.specification.AsyncSubscriber;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jay
 * 基于 Reactive-Stream标准的响应式
 * 2022/3/14
 */
public class SpecificationRsTest {

    @Test
    public void rsTest() {

        HashSet<Integer> elements = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            elements.add(i);
        }

        // 创建一个可重用固定线程数的线程池
        // 使用共享的无界队列方式来运行这些线程
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        AsyncIterablePublisher<Integer> publisher =
                new AsyncIterablePublisher<Integer>(elements, executorService);

        // 内部类形式
//        publisher.subscribe(new Subscriber<Integer>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//
//            }
//
//            @Override
//            public void onNext(Integer integer) {
//
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

        AsyncSubscriber<Integer> subscriber =
                new AsyncSubscriber<Integer>(Executors.newFixedThreadPool(2)) {
                    @Override
                    protected boolean whenNext(Integer element) {
                        System.out.println("接收到的流元素：" + element);
                        return true;
                    }
                };

        publisher.subscribe(subscriber);
    }

}
