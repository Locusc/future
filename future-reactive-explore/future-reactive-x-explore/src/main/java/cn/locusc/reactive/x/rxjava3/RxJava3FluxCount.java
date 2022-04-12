package cn.locusc.reactive.x.rxjava3;

import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RxJava3FluxCount {

    /**
     * 使用count转换响应式流
     */
    @Test
    public void test1() throws InterruptedException {

        List<Integer> list = Stream.iterate(0, i -> i + 1)
                .limit(1000)
                .collect(Collectors.toList());

        Observable.fromIterable(list)
                .filter(f -> f % 3 == 0)
                .count() // count生成新的响应式流, 该响应式流中只有一个元素就是上个流的元素个数
                .subscribe(System.out::println);

    }

}
