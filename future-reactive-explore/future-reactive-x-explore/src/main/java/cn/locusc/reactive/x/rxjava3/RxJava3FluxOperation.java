package cn.locusc.reactive.x.rxjava3;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RxJava3FluxOperation {

    /**
     * 创建响应式流(RxJava3), 根据array
     */
    public void test1() {
        Integer[] integers = {1, 2, 3};

        Observable<Integer> integerObservable = Observable.fromArray(integers);

        Disposable disposable = integerObservable.subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("流结束")
        );
    }

    /**
     * 创建响应式流(RxJava3), 可迭代的数据
     */
    public void test2() {
        List<Integer> list = Stream.iterate(0, i -> i + 1)
                .limit(10)
                .collect(Collectors.toList());

        Observable<Object> objectObservable = Observable.fromIterable(Collections.emptyList());

        Observable<Object> listObservable = Observable.fromIterable(list);
        objectObservable.subscribe(
                System.out::println,
                System.err::println
        );
    }


    /**
     * 创建响应式流(RxJava3), 使用Callable接口
     */
    public void test3() {
//        Observable.fromCallable(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return "hello flux";
//            }
//        }).subscribe(System.out::println);

        Observable.fromCallable(() -> "hello flux")
                .subscribe(System.out::println);
    }

    /**
     * 创建响应式流(RxJava3), Future对象
     */
    public void test4() {
        Future<String> future = Executors.newCachedThreadPool().submit(() -> "hello flux");

        // 从callable创建响应式流, 订阅, 消费
        Observable.fromFuture(future)
                .subscribe(System.out::println);
    }

}
