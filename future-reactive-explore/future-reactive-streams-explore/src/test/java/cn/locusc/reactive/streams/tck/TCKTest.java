package cn.locusc.reactive.streams.tck;

import cn.locusc.reactive.streams.specification.AsyncIterablePublisher;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * String表示要测试的发布者的流元素类型是String类型的
 */
public class TCKTest extends PublisherVerification<String> {

    public TCKTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<String> createPublisher(long l) {

        Set<Integer> set = Stream.iterate(0, i -> i + 1)
                .limit(10)
                .collect(Collectors.toSet());

        Set<String> s = Stream.generate(() -> String.valueOf(new Random().nextInt()))
                .limit(10)
                .collect(Collectors.toSet());

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        return new AsyncIterablePublisher<>(s, executorService);
    }

    @Override
    public Publisher createFailedPublisher() {
        HashSet<Object> set = new HashSet<>();
        set.add(new RuntimeException("handler error"));
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        return new AsyncIterablePublisher<>(set, executorService);
    }

    /**
     * 用于指定测试的响应式流有几个元素需要测试。默认是Long.MAX_VALUE - 1
     */
    @Override
    public long maxElementsFromPublisher() {
        // return super.maxElementsFromPublisher();
        return 10;
    }

}
