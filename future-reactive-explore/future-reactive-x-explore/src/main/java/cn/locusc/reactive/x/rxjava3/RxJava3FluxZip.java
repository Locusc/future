package cn.locusc.reactive.x.rxjava3;

import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

public class RxJava3FluxZip {

    /**
     * 使用zip转换响应式流 (组合器函数)
     * 没有对应的值不会被组合
     */
    @Test
    public void test1() throws InterruptedException {
        Observable.zip(
                Observable.just(1, 2, 3, 4, 5, 6),
                Observable.just("a", "b", "c", "d", "e"),
                (item1, item2) -> item2 + "-" + item1
        ).forEach(System.out::println);
    }

}
