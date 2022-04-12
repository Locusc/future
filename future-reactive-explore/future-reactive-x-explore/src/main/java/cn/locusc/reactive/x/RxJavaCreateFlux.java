package cn.locusc.reactive.x;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * @author Jay
 * 使用RxJava创建响应式流
 * 2022/3/14
 */
public class RxJavaCreateFlux {

    /**
     * 创建响应式流(内部类方式)
     */
    @Test
    @SuppressWarnings("DuplicatedCode")
    public void test1() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 0; i < 10; i++) {
                    subscriber.onNext("rx1 -- " + i);
                }
                subscriber.onCompleted();
            }

        });

        observable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("响应式流结束");
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("响应式流异常：" + throwable.getMessage());
            }

            @Override
            public void onNext(String s) {
                System.out.println("下一个响应式流元素：" + s);
            }
        });

    }

    /**
     * 创建响应式流(函数形式)
     */
    @SuppressWarnings("DuplicatedCode")
    public void test2() {
        Subscription subscription = Observable.create(
                subscriber -> {
                    for (int i = 0; i < 10; i++) {
                        // 传输响应式流中的元素
                        subscriber.onNext("rx1 -- " + i);
                    }
                    // 通知订阅者，响应式流结束
                    subscriber.onCompleted();
                }
        ).subscribe(
                item -> System.out.println("下一个元素是：" + item),
                ex -> System.err.println("异常信息：" + ex.getMessage()),
                () -> System.out.println("响应式流结束")
        );
    }

    /**
     * 创建响应式流(RxJava2)
     */
    public void test3() {
        io.reactivex.Observable<String> observable = io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception { }
        });
    }

    /**
     * 创建响应式流(RxJava3)
     */
    public void test4() {
        Disposable disposable = io.reactivex.rxjava3.core.Observable.create(
                (io.reactivex.rxjava3.core.ObservableOnSubscribe<String>) emitter -> {
                    for (int i = 0; i < 10; i++) {
                        emitter.onNext("rxjava3 - " + i);
                    }
                    emitter.onComplete();
                }).subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("流结束")
        );
    }

}
