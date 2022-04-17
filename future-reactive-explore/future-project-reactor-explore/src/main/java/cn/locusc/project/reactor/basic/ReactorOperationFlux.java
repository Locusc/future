package cn.locusc.project.reactor.basic;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Comparator;

/**
 * @author Jay
 * prject-reactor响应式流基础操作
 * 2022/3/17
 */
public class ReactorOperationFlux {

    /**
     * 映射响应式流元素
     * index 操作符可用于枚举序列中的元素。
     */
    @Test
    public void test() {
        Flux.range(1, 10)
                .map(m -> "hello jay - " + m)
                .index()
                .subscribe(System.out::println);
    }

    /**
     * 映射响应式流元素
     * timestamp 操作符的行为与 index 操作符类似，但会添加当前时间戳而不是索引。
     */
    @Test
    public void test1() {

        Flux<Tuple2<Long, String>> flux = Flux.range(1, 10)
                .map(m -> "hello jay - " + m)
                .timestamp();

        flux.subscribe(System.out::println);

        flux.subscribe(item -> System.out.println(item.getT1() + " <---> " + item.getT2()));

    }

    /**
     * 过滤响应式流
     * 1. filter 操作符仅传递满足条件的元素。
     * 2. ignoreElements 操作符返回 Mono<T> 并过滤所有元素。结果序列仅在原始序列结束后结
     * 束。 忽略onNext信号（丢弃它们）, 只传播终止事件。
     * 3. take(n) 操作符限制所获取的元素，该方法忽略除前 n 个元素之外的所有元素。
     * 4. takeLast 仅返回流的最后一个元素。
     * 5. takeUntil(Predicate) 传递一个元素直到满足某个条件。
     * 6. elementAt(n) 只可用于获取序列的第n个元素。
     * 7. single 操作符从数据源发出单个数据项, 也为空数据源发出NoSuchElementException错误
     * 信号, 或者为具有多个元素的数据源发出IndexOutOfBoundsException信号。它不仅可以基
     * 于一定数量来获取或跳过元素, 还可以通过带有Duration的 skip(Duration)或
     * take(Duration)操作符。
     * 8. takeUntilOther(Publisher)或skipUntilOther(Publisher)操作符, 可以跳过或获取一
     * 个元素, 直到某些消息从另一个流到达。
     */
    @Test
    public void test2() throws InterruptedException {
        System.out.println("开始");
        Mono<String> start = Mono.just("start").delayElement(Duration.ofSeconds(3));
        Mono<String> stop = Mono.just("stop").delayElement(Duration.ofSeconds(6));
        Flux.interval(Duration.ofMillis(500))
                .map(item -> "flux-element" + item)
                .skipUntilOther(start)
                .takeUntilOther(stop)
                .subscribe(System.out::println);
        Thread.sleep(10000);
    }

    /**
     * 收集响应式流
     * 请注意，收集集合中的序列元素可能耗费资源, 当序列具有许多元素时这种现象尤为突出。
     * 此外, 尝试在无限流上收集数据可能消耗所有可用的内存。
     */
    @Test
    public void test3() {
        Flux.just(1, 6, 2, 8, 3, 1, 5, 1)
                .collectSortedList(Comparator.reverseOrder())
                .subscribe(System.out::println);
    }

    /**
     * collectMap
     */
    @Test
    public void test4() {
        Flux.just(1, 2, 3, 4, 5)
                .collectMap(item -> "key:num - " + item)
                .subscribe(System.out::println);
    }

}
