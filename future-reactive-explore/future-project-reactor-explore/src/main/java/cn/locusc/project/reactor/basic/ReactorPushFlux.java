package cn.locusc.project.reactor.basic;

import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Jay
 * 以编程方式创建流
 * 2022/3/17
 */
public class ReactorPushFlux {

    /**
     * push
     * push 工厂方法能通过适配一个单线程生产者来编程创建 Flux 实例。
     * 此方法对于适配异步、单线程、多值 API 非常有用,
     * 而无须关注背压和取消, push方法本身包含背压和取消
     */
    @Test
    public void test1() throws InterruptedException {
        Flux.push(fluxSink -> IntStream.range(2000, 3000)
                .forEach(fluxSink::next)).delayElements(Duration.ofMillis(1))
        .subscribe(event -> System.out.println("onNext:" + event));

        Thread.sleep(5000);
    }

    /**
     * create 工厂方法, 与 push 工厂方法类似, 起到桥接的作用。
     * 该方法能从不同的线程发送事件。
     */
    @Test
    public void test2() throws InterruptedException {
        MyEventProcessor myEventProcessor = new MyEventProcessor();

        Flux<String> bridge = Flux.create(fluxSink -> {
            myEventProcessor.register(
                    new MyEventListener<String>() {
                        @Override
                        public void onDataChunk(List<String> chunk) {
                            for (String s : chunk) {
                                fluxSink.next(s);
                            }
                        }

                        @Override
                        public void processComplete() {
                            fluxSink.complete();
                        }
                    }
            );
        });

        bridge.subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("处理完成")
        );

        myEventProcessor.process();

        Thread.sleep(5000);
    }

    interface MyEventListener<T> {
        void onDataChunk(List<T> chunk);
        void processComplete();
    }

    static class MyEventProcessor {
        private MyEventListener listener;
        private Random random = new Random();
        void register(MyEventListener listener) {
            this.listener = listener;
        }
        public void process() {
            while (random.nextInt(10) % 3 != 0) {
                List<String> dataChunk = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    dataChunk.add("data-" + i);
                }
                listener.onDataChunk(dataChunk);
            }
            listener.processComplete();
        }
    }

    /**
     * generate 工厂方法
     * generate 工厂方法旨在基于生成器的内部处理状态创建复杂序列。
     * 它需要一个初始值和一个函数, 该函数根据前一个内部状态计算下一个状态,
     * 并将 onNext 信号发送给下游订阅者
     */
    @Test
    public void test3() throws InterruptedException {
//        Flux.generate(
//                // 通过Callable提供初始状态实例
//                new Callable<ArrayList<Long>>() {
//                    @Override
//                    public ArrayList<Long> call() throws Exception {
//                        final ArrayList<Long> longs = new ArrayList<>();
//                        longs.add(0L);
//                        longs.add(1L);
//                        return longs;
//                    }
//                },
//                // 负责生成斐波拉契数列
//                // 函数的第一个参数类型，函数第二个参数类型，函数计算结果类型
//                new BiFunction<ArrayList<Long>, SynchronousSink<Long>,
//                                        ArrayList<Long>>() {
//                    @Override
//                    public ArrayList<Long> apply(ArrayList<Long> longs,
//                                                 SynchronousSink<Long> sink) {
//                        final Long aLong = longs.get(longs.size() - 1);
//                        final Long aLong1 = longs.get(longs.size() - 2);
//                        // 向下游发射流元素
//                        sink.next(aLong);
//                        longs.add(aLong + aLong1);
//                        return longs;
//                    }
//                }
//        ).delayElements(Duration.ofMillis(500))
//                .take(10)
//                .subscribe(System.out::println);
//        Thread.sleep(5000);

        Flux<Long> fibonacci = Flux.generate(
                () -> {
                    ArrayList<Long> longs = new ArrayList<>();
                    longs.add(0L);
                    longs.add(1L);
                    return longs;
                },
                (state, sink) -> {
                    Long aLong = state.get(state.size() - 1);
                    Long aLong1 = state.get(state.size() - 2);
                    // 发射流元素
                    sink.next(aLong);
                    state.add(aLong + aLong1);
                    return state;
                }
        );

        // 状态还可以记录为二元组
        Flux<Long> generate = Flux.generate(
                () -> Tuples.of(0L, 1L),
                (state, sink) -> {
                    System.out.println("生成的数字：" + state.getT2());
                    sink.next(state.getT2());
                    long newValue = state.getT1() + state.getT2();
                    return Tuples.of(state.getT2(), newValue);
                }
        );

        generate.delayElements(Duration.ofMillis(500))
                .take(10)
                .subscribe(System.out::println);

        Thread.sleep(5000);
    }

    /**
     * 将 disposable 资源包装到响应式流中
     * using 工厂方法能根据一个 disposable 资源创建流。
     * 它在响应式编程中实现了 try-with-resources 方法。
     */
    @Test
    public void test4() {
        try (Connection connection = Connection.newConnection()) {
            connection.getData()
                    .forEach(f -> System.out.println("接收的数据：" + f));
        } catch (Exception e) {
            System.out.println("错误信息:" + e);
        }

        Flux.using(
                Connection::newConnection,
                connection -> Flux.fromIterable(connection.getData()),
                Connection::close
        ).subscribe(
                data -> System.out.println("onNext接收到的数据：" + data),
                ex -> System.err.println("onError接收到的异常信息：" + ex),
                () -> System.out.println("处理完毕")

        );

        // 连接的生命周期与流的生命周期绑定。
        // 操作符还可以在通知订阅者流终止之前或之后选择是否应该进行清除动作。
    }

    static class Connection implements AutoCloseable {
        private final Random rnd = new Random();
        static Connection newConnection() {
            System.out.println("创建Connection对象");
            return new Connection();
        }
        public Iterable<String> getData() {
            if (rnd.nextInt(10) < 3) {
                throw new RuntimeException("通信异常");
            }
            return Arrays.asList("数据1", "数据2");
        }
        // close方法可以释放内部资源，并且应该始终被调用，即使在getData执行期间发生错误也是如此。
        @Override
        public void close() {
            System.out.println("关闭Connection连接");
        }
    }


    /**
     * 与 using 操作符类似，usingWhen 操作符使我们能以响应式方式管理资源。但是，using操作符会
     * 同步获取受托管资源（通过调用 Callable 实例）。同时，usingWhen 操作符响应式地获取受托管资源
     * （通过订阅 Publisher 的实例）。此外，usingWhen 操作符接受不同的处理程序，以便应对主处理流终
     * 止的成功和失败。这些处理程序由发布者实现。
     * 可以仅使用usingWhen一个操作符实现完全无阻塞的响应式事务。
     */
    @Test
    public void test5() throws InterruptedException {
        //        Flux.usingWhen(
//                // 提供资源
//                Transaction.beginTransaction(),
//
//                // 资源的使用
//                new Function<Transaction, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(Transaction transaction) {
//                        return transaction.insertRows(Flux.just("a", "b", "c"));
//                    }
//                },
//
//                // 当资源正常使用结束，调用了onComplete，则使用该函数清理资源
//                new Function<Transaction, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(Transaction transaction) {
//                        return transaction.commit();
//                    }
//                },
//
//                // 当出现错误，可以在这里处理
//                new BiFunction<Transaction, Throwable, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(Transaction transaction, Throwable throwable) {
//                        return transaction.rollback();
//                    }
//                },
//                // 如果取消资源的使用，则使用该函数清理资源。如果设置为null，则使用资源正常结束时的清理函数
//                new Function<Transaction, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(Transaction transaction) {
//                        return null;
//                    }
//                }
//        ).subscribe(
//                event -> System.out.println("onNext:" + event),
//                ex -> System.err.println("onError:" + ex.getCause()),
//                () -> System.out.println("处理完成")
//        );

        Flux.usingWhen(
                Transaction.beginTransaction(),
                transaction -> transaction.insertRows(Flux.just("A", "B", "C")),
                Transaction::commit,
                (transaction, throwable) -> transaction.rollback(),
                Transaction::rollback
        ).subscribe(
                event -> System.out.println("onNext:" + event),
                ex -> System.err.println("onError:" + ex),
                () -> System.out.println("处理完成")
        );

        Thread.sleep(5000);
    }

    /**
     * 将 disposable 资源包装到响应式流中
     * 与 using 操作符类似，usingWhen 操作符使我们能以响应式方式管理资源。但是，using操作符会
     * 同步获取受托管资源（通过调用 Callable 实例）。同时，usingWhen 操作符响应式地获取受托管资源
     * （通过订阅 Publisher 的实例）。此外，usingWhen 操作符接受不同的处理程序，以便应对主处理流终
     * 止的成功和失败。这些处理程序由发布者实现。
     * 可以仅使用 usingWhen 一个操作符实现完全无阻塞的响应式事务。
     */
    static class Transaction {
        private static final Random random = new Random();
        private final int id;
        /**
         * 创建事务对象
         */
        public Transaction(int id) {
            this.id = id;
            System.out.println("创建事务实例：" + id);
        }
        /**
         * 开启响应式事务
         */
        public static Mono<Transaction> beginTransaction() {
            return Mono.defer(() ->
                    Mono.just(new Transaction(random.nextInt(1000))));
        }
        /**
         * 响应式插入数据
         */
        public Flux<String> insertRows(Publisher<String> rows) {
            return Flux.from(rows)
                    .delayElements(Duration.ofMillis(100))
                    .flatMap(row -> {
                        if (random.nextInt(10) < 2) {
                            return Mono.error(new RuntimeException("出错的条目: " + row));
                        } else {
                            return Mono.just(row);
                        }
                    });
        }
        /**
         * 响应式提交
         */
        public Mono<Void> commit() {
            return Mono.defer(() -> {
                System.out.println("[开始提交事务：" + id);
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("事务提交异常"));
                }
            });
        }
        /**
         * 响应式回滚
         */
        public Mono<Void> rollback() {
            return Mono.defer(() -> {
                System.out.println("开始回滚事务：" + id);
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("连接异常"));
                }
            });
        }
    }
}
