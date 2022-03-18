package cn.locusc.reactive.streams.tck;

import cn.locusc.reactive.streams.specification.AsyncSubscriber;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.tck.SubscriberWhiteboxVerification;
import org.reactivestreams.tck.TestEnvironment;

import java.util.concurrent.Executors;

/**
 * @author Jay
 * tck白盒测试
 * 2022/3/14
 */
public class TCKWhiteBoxTest extends SubscriberWhiteboxVerification<Integer> {

    protected TCKWhiteBoxTest() {
        super(new TestEnvironment());
    }

    @Override
    public Subscriber<Integer> createSubscriber(WhiteboxSubscriberProbe<Integer> probe) {
        AsyncSubscriber subscriber = new AsyncSubscriber(Executors.newFixedThreadPool(2)) {
            @Override
            protected boolean whenNext(Object o) {
                return false;
            }

            @Override
            public void onSubscribe(Subscription subscription) {
                super.onSubscribe(subscription);
                probe.registerOnSubscribe(new SubscriberPuppet() {
                    @Override
                    public void triggerRequest(long elements) {
                        subscription.request(elements);
                    }

                    @Override
                    public void signalCancel() {
                        subscription.cancel();
                    }
                });
            }

            @Override
            public void onNext(Object element) {
                super.onNext(element);
                // 注册钩子
                probe.registerOnNext((Integer) element);
            }

            @Override
            public void onError(Throwable t) {
                super.onError(t);
                probe.registerOnError(t);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                probe.registerOnComplete();
            }


        };
        return null;
    }

    @Override
    public Integer createElement(int element) {
        return element;
    }

}
