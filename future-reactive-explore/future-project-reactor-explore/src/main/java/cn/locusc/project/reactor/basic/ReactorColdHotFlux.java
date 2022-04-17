package cn.locusc.project.reactor.basic;

import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

/**
 * @author Jay
 * 冷热数据流转换
 * 2022/3/18
 */
public class ReactorColdHotFlux {

    /**
     * 冷发布者行为方式: 无论订阅者何时出现, 都为该订阅者生成所有序列数据, 没有订阅者就不会生
     * 成数据。
     * 以下代码表示冷发布者的行为:
     */
    @Test
    public void test1() {
        Flux<String> coldPublisher = Flux.defer(() -> {
            System.out.println("生成新数据");
            return Flux.just(UUID.randomUUID().toString());
        });
        System.out.println("尚未生成新数据");
        coldPublisher.subscribe(e -> System.out.println("onNext: " + e));
        coldPublisher.subscribe(e -> System.out.println("onNext: " + e));
        System.out.println("为两个订阅者生成了两次数据");
    }

    /**
     * 多播流元素
     * 通过响应式转换将冷发布者转变为热发布者
     * 如，一旦所有订阅者都准备好生成数据，希望在几个订阅者之间共享冷处理器的结果。同时，我们
     * 又不希望为每个订阅者重新生成数据。Project Reactor为此目的提供了 ConnectableFlux 。
     * ConnectableFlux ，不仅可以生成数据以满足最急迫的需求，还会缓存数据，以便所有其他订阅
     * 者可以按照自己的速度处理数据。队列和超时的大小可以通过类的 publish 方法和 replay 方法进行配
     * 置。
     * 此外， ConnectableFlux 可以使用 connect 、 autoConnect(n) 、 refCount(n) 和
     * refCount(int,Duration) 等方法自动跟踪下游订阅者的数量，以便在达到所需阈值时触发执行操
     * 作。
     */
    @Test
    public void test2() {
        Flux<Integer> source = Flux.range(0, 3)
                .doOnSubscribe(
                        s -> System.out.println("对冷发布者的新订阅票据：" + s)
                );
        ConnectableFlux<Integer> conn = source.publish();
        conn.subscribe(item -> System.out.println("[Subscriber 1] onNext:" + item));
        conn.subscribe(item -> System.out.println("[Subscriber 2] onNext:" + item));
        System.out.println("所有订阅者都准备好建立连接了");
        conn.connect();
    }

    /**
     * 缓存流元素
     * 使用 ConnectableFlux 可以轻松实现不同的数据缓存策略。但是，Reactor 已经以 cache 操作
     * 符的形式提供了用于事件缓存的 API。
     * cache 操作符使用 ConnectableFlux, 因此它的主要附加值是它所提供的一个流式而直接的
     * API。
     * 可以调整缓存所能容纳的数据量以及每个缓存项的到期时间。
     */
    @Test
    public void test3() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 5)
                .doOnSubscribe(
                        s -> System.out.println("冷发布者的新订阅票据")
                );
        Flux<Integer> cachedSource = source.cache(Duration.ofMillis(1000));
        cachedSource.subscribe(item -> System.out.println("[S 1] onNext: " + item));
        cachedSource.subscribe(item -> System.out.println("[S 2] onNext: " + item));
        Thread.sleep(5000);
        cachedSource.subscribe(item -> System.out.println("[S 3] onNext: " + item));
    }

    /**
     * 共享数据流
     * 我们可以使用 ConnectableFlux 向几个订阅者多播事件。但是需要等待订阅者出现才能开始处
     * 理。
     * share 操作符可以将冷发布者转变为热发布者。该操作符会为每个新订阅者传播订阅者尚未错过的
     * 事件。
     *
     * 代码中，共享了一个冷发布流，该流以每 100 毫秒为间隔生成事件。然后, 经过一些延
     * 迟, 一些订阅者订阅了共享发布者。
     * 第一个订阅者从第一个事件开始接收, 而第二个订阅者错过了在其出现之前所产生的事件（S2 仅接
     * 收到事件 3 和事件 4）。
     */
    @Test
    public void test4() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 5)
                .delayElements(Duration.ofMillis(100))
                .doOnSubscribe(s -> System.out.println("冷发布者新的订阅票据"));
        Flux<Integer> cachedSource = source.share();
        cachedSource.subscribe(item -> System.out.println("[S 1] onNext: " + item));
        Thread.sleep(400);
        cachedSource.subscribe(item -> System.out.println("[S 2] onNext: " + item));
        Thread.sleep(1000);
    }

}
