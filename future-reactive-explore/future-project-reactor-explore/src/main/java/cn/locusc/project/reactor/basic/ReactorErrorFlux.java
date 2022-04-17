package cn.locusc.project.reactor.basic;

import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jay
 * project-reactor错误处理
 * 2022/3/18
 */
public class ReactorErrorFlux {

    /**
     * onError信号是响应式流规范的一个组成部分, 一种将异常传播给可以处理它的用户。但是, 如果
     * 最终订阅者没有为 onError 信号定义处理程序, 那么 onError 抛异常。
     * 响应式流的语义定义了 onError 是一个终止操作, 该操作之后响应式流会停止执行。
     */
    @Test
    public void test1() {
        Flux.from(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> s) {
                s.onError(new RuntimeException("手动异常"));
            }
            // }).subscribe(System.out::println, System.err::println);
        }).subscribe(System.out::println);
    }

    /**
     * 我们可能采取以下策略中的一种做出不同响应:
     * 1. 为 subscribe 操作符中的 onError 信号定义处理程序。
     * 2. 通过 onErrorReturn 操作符捕获一个错误, 并用一个默认静态值或一个从异常中计算出的值
     * 替换它。
     * 3. 通过 onErrorResume 操作符捕获异常并执行备用工作流。
     * 4. 通过 onErrorMap 操作符捕获异常并将其转换为另一个异常来更好地表现当前场景。
     * 5. 定义一个在发生错误时重新执行的响应式工作流。如果源响应序列发出错误信号, 那么
     * retry 操作符会重新订阅该序列。
     */
    @Test
    public void test2() throws InterruptedException {
//        Flux.just("user-0010")
//            .flatMap(user -> recommendedBooks(user)).retry(3) // 最多发送四次调用，其中后三次是重试
//            .subscribe(
//                System.out::println,
//                ex -> {
//                    System.err.println(ex);
//                    latch.countDown();
//                },
//                () -> {
//                    System.out.println("处理完成");
//                    latch.countDown();
//                }
//            );

//        Flux.just("user-0010")
//            .flatMap(user -> recommendedBooks(user))
//            .onErrorResume(event -> Flux.just("java编程指南.pdf")) // 一旦流中出现异常，则使用指定的流替换
//            .subscribe(
//                    System.out::println,
//                    ex -> {
//                        System.err.println(ex);
//                        latch.countDown();
//                    },
//                    () -> {
//                        System.out.println("处理完成");
//                        latch.countDown();
//                    }
//            );

//        Flux.just("user-0010")
//            .flatMap(user -> recommendedBooks(user))
//            .onErrorReturn("程序员生存之道.pdf") // 一旦流中出现onError，则使用指定的元素替代
//            .subscribe(
//                    System.out::println,
//                    ex -> {
//                        System.err.println(ex);
//                        latch.countDown();
//                    },
//                    () -> {
//                        System.out.println("处理完成");
//                        latch.countDown();
//                    }
//            );

        Flux.just("user-0010")
                .flatMap(user -> recommendedBooks(user))
                .onErrorMap(throwable -> {
                    if (throwable.getMessage().equals("Err")) {
                        return new Exception("我的异常替换");
                    }
                    return new Exception("未知异常");
                })
                .subscribe(
                        System.out::println,
                        ex -> {
                            System.err.println(ex);
                            latch.countDown();
                        },
                        () -> {
                            System.out.println("处理完成");
                            latch.countDown();
                        }
                );
        latch.await();
    }

    private static Random random = new Random();
    private static CountDownLatch latch = new CountDownLatch(1);

    public static Flux<String> recommendedBooks(String userId) {
        return Flux.defer(() -> {
            if (random.nextInt(10) < 7) {
                return Flux.<String>error(new RuntimeException("Err"))
                        // 整体向后推移指定时间，元素发射频率不变
                        .delaySequence(Duration.ofMillis(100));
            } else {
                return Flux.just("Blue Mars", "The Expanse")
                        .delayElements(Duration.ofMillis(50));
            }
        }).doOnSubscribe(
                item -> System.out.println("请求：" + userId)
        );
    }

}
