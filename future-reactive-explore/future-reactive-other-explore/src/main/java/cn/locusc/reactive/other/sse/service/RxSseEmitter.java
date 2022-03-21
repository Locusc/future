package cn.locusc.reactive.other.sse.service;

import org.json.JSONObject;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import rx.Subscriber;

import java.io.IOException;

/**
 * @author Jay
 * 使用RxJava实现SSE(server-send-event)
 * 2022/3/16
 */
public class RxSseEmitter<T> extends SseEmitter {

    static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000;

    private final Subscriber<T> subscriber;

    public RxSseEmitter() {
        super(SSE_SESSION_TIMEOUT);
        subscriber = new Subscriber<T>() {

            @Override
            public void onCompleted() {
                System.out.println("处理结束");
            }

            @Override
            public void onError(Throwable e) {
                System.err.println("发生异常：" + e.getMessage());
            }

            @Override
            public void onNext(T t) {
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    String s = jsonObject.toString();
                    System.out.println(s);
                    RxSseEmitter.this.send(s);
                } catch (IOException e) {
                    this.unsubscribe();
                }
            }
        };
    }

    public Subscriber<T> getSubscriber() {
        return this.subscriber;
    }
}
