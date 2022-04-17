package cn.locusc.project.reactor.basic;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jay
 * project-reactor背压处理
 * 2022/3/18
 */
public class ReactorBackPressureFlux {

    /**
     * onBackpressureBuffer
     * 缓冲队列
     */
    @Test
    public void test1() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 1000)
                .delayElements(Duration.ofMillis(10))
                .onBackpressureBuffer(900)
                .delayElements(Duration.ofMillis(100))
                .subscribe(
                        System.out::println,
                        ex -> {
                            System.out.println(ex);
                            latch.countDown();
                        },
                        () -> {
                            System.out.println("处理完成");
                            latch.countDown();
                        }
                );
        latch.await();
        System.out.println("main结束");
    }

    /**
     * onBackpressureDrop
     * 下游无法处理时抛弃
     */
    @Test
    public void test2() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 1000)
                .delayElements(Duration.ofMillis(10))
                .onBackpressureDrop()
                .delayElements(Duration.ofMillis(100))
                .subscribe(
                        System.out::println,
                        ex -> {
                            System.out.println(ex);
                            latch.countDown();
                        },
                        () -> {
                            System.out.println("处理完成");
                            latch.countDown();
                        }
                );
        latch.await();
        System.out.println("main结束");
    }

    /**
     * onBackpressureLatest
     * 或者如果下游没有足够的请求, 只保留最近观察到的项目。
     */
    @Test
    public void test3() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 1000)
                .delayElements(Duration.ofMillis(10))
                .onBackpressureLatest()
                .delayElements(Duration.ofMillis(100))
                .subscribe(
                        System.out::println,
                        ex -> {
                            System.out.println(ex);
                            latch.countDown();
                        },
                        () -> {
                            System.out.println("处理完成");
                            latch.countDown();
                        }
                );
        latch.await();
        System.out.println("main结束");
    }

    /**
     * onBackpressureError
     * 或者如果下游没有足够的请求, 则发出一个错误的fom{@link Exceptions#failWithOverflow}。
     */
    @Test
    public void test4() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 1000)
                .delayElements(Duration.ofMillis(10))
                .onBackpressureError()
                .delayElements(Duration.ofMillis(100))
                .subscribe(
                        System.out::println,
                        ex -> {
                            System.out.println(ex);
                            latch.countDown();
                        },
                        () -> {
                            System.out.println("处理完成");
                            latch.countDown();
                        }
                );
        latch.await();
        System.out.println("main结束");
    }
}
