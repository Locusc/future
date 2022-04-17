package cn.locusc.project.reactor.basic;

import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.List;

/**
 * @author Jay
 * Reactor Flux
 * 2022/3/16
 */
public class ReactorCreateFlux {

    @Test
    public void test1() {
        Flux<String> flux = null;
        Mono<String> mono = null;

        Publisher<String> publisher = null;
        Subscriber<String> subscriber = null;
        Subscription subscription = null;
    }


    /**
     * block用法未知
     */
    @Test
    public void test2() {
        // range操作符创建从[1到100]的整数序列
        List<Integer> block = Flux.range(1, 100)
                // repeat操作符在源流完成之后一次又一次地订阅源响应式流。
                // repeat操作符订阅流操作符的结果、接收从1到100的元素以及onComplete信号，
                // 然后再次订阅、接收,不断重复该过程
                .repeat()
                // 使用collectList操作符尝试将所有生成的元素收集到一个集合中。
                // 由于是无限流, 最终它会消耗所有内存, 导致OOM。
                .collectList()
                // block操作符触发实际订阅并阻塞正在运行的线程, 直到最终结果到达
                // 当前场景不会发生, 因为响应流是无限的。
                .block();
    }

    /**
     * 使用Flux.just创建响应式流
     */
    @Test
    public void test3() {
        Flux.just("hi", "jay")
                .subscribe(
                    item -> System.out.println("onNext：" + item),
                    ex -> System.out.println("onError：" + ex),
                    () -> System.out.println("处理结束")
        );
    }

    /**
     * 使用Flux.range创建响应式流
     */
    @Test
    public void test4() {
        Flux.range(1, 100)
                .filter(f -> f % 7 == 0)
                .map(m -> "jay-" + m)
                .doOnNext(System.out::println)
                .subscribe();
    }

    /**
     * 使用Flux.from创建响应式流
     */
    @Test
    public void test5() {
        Flux.from(subscriber -> {
            for (int i = 0; i < 10; i++) {
                subscriber.onNext(i);
            }
            subscriber.onError(new Exception("异常测试"));
        }).subscribe(
                System.out::println,
                ex -> System.out.println("异常处理：" + ex)
        );
    }

    /**
     * 取消订阅
     */
    @Test
    public void test6() {
        Flux.range(1, 10)
                .doOnCancel(() -> System.out.println("doOnCancel"))
                .subscribe(
                    System.out::println,
                    System.err::println,
                    () -> System.out.println("处理结束"),
                    Subscription::cancel // 只要订阅成功，立刻取消订阅
                 );
    }

    /**
     * request请求拉取元素
     * 手动订阅控制
     */
    @Test
    public void test7() {
        Flux.range(1, 100).subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("处理结束"),
                subscription -> {
                    // 手动控制请求五个元素
                    subscription.request(5);
                    // 取消订阅
                    subscription.cancel();
                }
        );
    }

    /**
     * Disposable取消订阅
     */
    @Test
    public void test8() throws InterruptedException {
        Disposable disposable = Flux.interval(Duration.ofMillis(500))
                .subscribe(System.out::println);

        Thread.sleep(50000);
        // 取消订阅
        disposable.dispose();
    }


    /**
     * 自定义订阅者
     * 不符合tck规范
     */
    @Test
    public void test9() throws InterruptedException {
        Flux.range(1, 100)
                .subscribe(new Subscriber<Integer>() {

                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        // 一旦订阅成功，则回调该方法
                        // 请求一个元素
                        s.request(1);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(integer);
                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(t);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("处理结束");
                    }
                });
    }

    /**
     * 自定义订阅者BaseSubscriber
     * 符合tck规范的钩子函数
     */
    @Test
    public void test10() throws InterruptedException {
        Flux.range(1, 100)
                .subscribe(new BaseSubscriber<Integer>() {

                    private Subscription subscription;

                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {

                        this.subscription = subscription;

                        // onSubscribe的回调
                        System.out.println("订阅成功");
                        subscription.request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println("onNext : " + value);
                        subscription.request(1);
                    }

                    @Override
                    protected void hookOnComplete() {
                        System.out.println("处理结束");
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
                        super.hookOnCancel();
                    }

                    @Override
                    protected void hookFinally(SignalType type) {
                        super.hookFinally(type);
                    }
                });
    }

}
