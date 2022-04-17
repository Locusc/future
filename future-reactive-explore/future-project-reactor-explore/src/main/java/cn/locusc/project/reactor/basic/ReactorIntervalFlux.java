package cn.locusc.project.reactor.basic;

import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jay
 * 响应式编程是异步的, 因此它本身就假定存在时序。
 * 基于 Project Reactor, 可以使用 interval 操作符生成基于一定持续时间的事件, 使用
 * delayElements 操作符生成延迟元素, 并使用 delaySequence 操作符延迟所有信号。
 * Reactor的API 使你能对一些与时间相关的事件做出响应， timestamp 操作符用于输出元素的时间
 * 戳, timeout 操作符用于指定消息时间间隔的大小。与 timestamp 类似, elapsed 操作符测量与上
 * 一个事件的时间间隔。
 * 2022/3/18
 */
public class ReactorIntervalFlux {

    /**
     * request请求拉取元素
     */
    @Test
    public void test() throws InterruptedException {
        Disposable disposable = Flux.interval(Duration.ofMillis(500))
                .subscribe(System.out::println);

        Thread.sleep(50000);
        // 取消订阅
        disposable.dispose();
    }

    /**
     * interval 指定每个元素之间的时间间隔
     */
    @Test
    public void test1() throws InterruptedException {
        Flux.interval(Duration.ofMillis(100))
                .subscribe(
                        item -> {
                            System.out.println(Thread.currentThread().getName() + " -- " + item);
                        }
                );

        System.out.println(Thread.currentThread().getName());

        // 指定第一个元素发送的时间与订阅时间的时间差
        // 第二个指定生成的序列元素之间的时间间隔
        Flux.interval(Duration.ofSeconds(3), Duration.ofMillis(100))
                .subscribe(System.out::println);

        Flux.interval(Duration.ofMillis(100), Schedulers.parallel())
                .subscribe(
                        item -> {
                            System.out.println(Thread.currentThread().getName() + " -- " + item);
                        }
                );

        Flux.interval(Duration.ofMillis(100), Schedulers.newSingle("count"))
                .subscribe(
                        item -> {
                            System.out.println(Thread.currentThread().getName() + " -- " + item);
                        }
                );

        Thread.sleep(1000);
        System.out.println("结束");
    }

    /**
     * delayElements和delaySequence
     */
    @Test
    public void test2() throws InterruptedException {
        Flux.range(1, 1000)
                // 指定元素之间时间间隔
                .delayElements(Duration.ofSeconds(1))
                .subscribe(item -> {
                    System.out.println(Thread.currentThread().getName() + " -- " + item);
                });

        Flux.range(1, 1000)
                // 指定订阅时间和第一个元素发布时间的时间间隔
                .delaySequence(Duration.ofSeconds(3))
                .subscribe(item -> {
                    System.out.println(Thread.currentThread().getName() + " -- " + item);
                });

        Thread.sleep(5000);
        System.out.println("结束");
    }

    /**
     * timeout
     */
    @Test
    public void test3() throws InterruptedException {
        Random random = new Random();
        CountDownLatch latch = new CountDownLatch(1);
        Flux.interval(Duration.ofMillis(300))
                .timeout(Duration.ofMillis(random.nextInt(20) + 290))
                .subscribe(
                        System.out::println,
                        ex -> {
                            System.err.println(ex);
                            latch.countDown();
                        },
                        () -> latch.countDown()
                );
        latch.await(10, TimeUnit.SECONDS);
    }

    /**
     * timestamp
     */
    @Test
    public void test4() throws InterruptedException {
        Flux.interval(Duration.ofMillis(300))
                .timestamp()
                .subscribe(item -> {
                    Long timestamp = item.getT1();
                    Long element = item.getT2();
                    String result = element + "的时间戳：" + timestamp;
                    System.out.println(result);
                });
        Thread.sleep(5000);
    }

    /**
     * elapsed
     * 从前面的输出中可以明显看出，事件并未恰好在 300 毫秒的时间间隔内到达。
     * 发生这种情况是因为 Reactor 使用 Java 的 ScheduledExecutorService 进行调度事件，而这些事件
     * 本身并不能保证精确的延迟。
     * 因此，应该注意不要在 Reactor 库中要求太精确的时间（实时）间隔。
     */
    @Test
    public void test5() throws InterruptedException {
        Flux.interval(Duration.ofMillis(300))
                .elapsed()
                .subscribe(item -> {
                    Long interval = item.getT1();
                    Long element = item.getT2();
                    String result = element + " 与上一个元素的时间间隔：" + interval + "ms";
                    System.out.println(result);
                });
        Thread.sleep(5000);
    }

}
