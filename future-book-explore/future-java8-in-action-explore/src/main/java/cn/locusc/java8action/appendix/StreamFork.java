package cn.locusc.java8action.appendix;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Jay
 * 多个Stream差分异步处理
 * 不可用于文件操作
 * 2021/12/5
 */
public class StreamFork<T> {

    private final Stream<T> stream;

    // 存放所有的Stream
    private final Map<Object, Function<Stream<T>, ?>> forks = new HashMap<>();

    public StreamFork(Stream<T> stream) {
        this.stream = stream;
    }

    public StreamFork<T> fork(Object key, Function<Stream<T>, ?> f) {
        forks.put(key, f);
        return this;
    }

    // 获取异步执行结果
    public interface Results {
        <R> R get(Object key);
    }

    private static class ForkingStreamConsumer<T> implements Consumer<T>, Results {

        static final Object END_OF_STREAM = new Object();
        private final List<BlockingQueue<T>> queues;
        private final Map<Object, Future<?>> actions;

        ForkingStreamConsumer(List<BlockingQueue<T>> queues, Map<Object, Future<?>> actions) {
            this.queues = queues;
            this.actions = actions;
        }

        @Override
        public <R> R get(Object key) {
            try {
                return ((Future<R>) actions.get(key)).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void accept(T t) {
            queues.forEach(q -> q.add(t));
        }

        void finish() {
            this.accept((T) END_OF_STREAM);
        }

    }

    public Results getResults() {
        ForkingStreamConsumer<T> consumer = build();
        try {
            stream.sequential().forEach(consumer);
        } finally {
            consumer.finish();
        }
        return consumer;
    }

    private ForkingStreamConsumer<T> build() {
        // 队列数量等于提交的任务数量
        List<BlockingQueue<T>> queues = new ArrayList<>();
        // 建立用于标识操作的键与包含操作结果的Future之间的映射关系
        Map<Object, Future<?>> actions =
                forks.entrySet().stream().reduce(
                        new HashMap<>(),
                        (map, e) -> {
                            map.put(e.getKey(),
                                    getOperationResult(queues, e.getValue()));
                            return map;
                        },
                        (m1, m2) -> {
                            m1.putAll(m2);
                            return m1;
                        });

        return new ForkingStreamConsumer<>(queues, actions);
    }

    private Future<?> getOperationResult(List<BlockingQueue<T>> queues, Function<Stream<T>, ?> f) {
        // 每一个队列对应一个操作
        BlockingQueue<T> queue = new LinkedBlockingQueue<>();
        queues.add(queue);
        // 利用差分器 遍历队列中的元素
        Spliterator<T> spliterator = new BlockingQueueSpliterator<>(queue);
        Stream<T> source = StreamSupport.stream(spliterator, false);
        return CompletableFuture.supplyAsync(() -> f.apply(source));
    }


    private static class BlockingQueueSpliterator<T> implements Spliterator<T> {

        private final BlockingQueue<T> q;

        BlockingQueueSpliterator(BlockingQueue<T> q) {
            this.q = q;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            T t;
            for(;;) {
                try {
                    t = q.take();
                    break;
                }
                catch (InterruptedException ignored) {
                }
            }
            if (t != ForkingStreamConsumer.END_OF_STREAM) {
                action.accept(t);
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }
    }
}
