package cn.locusc.reactive.x.rxjava3;

import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RxJava3FluxFilter {

    /**
     * 使用Filter转换响应式流
     */
    @Test
    public void test1() throws InterruptedException {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .filter(item -> item % 3 == 0)
                .forEach(System.out::println);

        Thread.sleep(10000);
    }

}
