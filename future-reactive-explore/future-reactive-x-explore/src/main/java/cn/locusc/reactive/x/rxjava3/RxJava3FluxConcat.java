package cn.locusc.reactive.x.rxjava3;

import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

public class RxJava3FluxConcat {

    /**
     * 响应式流拼接
     */
    @Test
    public void test1() {
        Observable<Integer> observable = Observable.concat(
                Observable.just(1, 2, 3, 4, 5),
                Observable.fromArray(new Integer[]{11, 12, 13, 14}),
                Observable.create(
                        subscriber -> {
                            for (int i = 0; i < 5; i++) {
                                subscriber.onNext(100 + i);
                            }
                            subscriber.onComplete();
                        }
                )
        );

        observable.subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("流结束")
        );

        observable.forEach(System.out::println);
    }

}
