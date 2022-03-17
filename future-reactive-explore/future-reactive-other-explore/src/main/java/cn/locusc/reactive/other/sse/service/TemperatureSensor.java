package cn.locusc.reactive.other.sse.service;

import cn.locusc.reactive.other.sse.entity.Temperature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TemperatureSensor {

    private final ApplicationEventPublisher publisher;
    private final Random random = new Random();
    // 1.返回一个线程池,只有一个线程
    // 2.可以在旧的线程挂掉之后, 重新启动一个新的线程来替代它.  达到起死回生的效果。
    // 3.满足任务执行(单线程的调度任务)
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public TemperatureSensor(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    // @PostConstruct
    public void startProcessing() {
        this.service.schedule(this::probe, 1, TimeUnit.SECONDS);
    }

    private void probe() {
        // 伪随机高斯分布双精度数
        double temperature = 16 + random.nextGaussian() * 10;
        System.err.println("发送事件-----");
        // 通过ApplicationEventPublisher发布Temperature事件
        publisher.publishEvent(new Temperature(temperature));
        service.schedule(this::probe, random.nextInt(5000), TimeUnit.MILLISECONDS);
    }

    private final Observable<Temperature> dataStream =
            Observable.range(0, Integer.MAX_VALUE)
                .concatMap(tick -> Observable.just(tick)
                        .delay(random.nextInt(5000), TimeUnit.MILLISECONDS)
                        .map(m -> this.nextGaussian())
                        // .share()
                        .publish()
                        .refCount()
                );

    /**
     * 伪随机高斯分布双精度数
     */
    private Temperature nextGaussian() {
        return new Temperature(16 + random.nextGaussian() * 10);
    }

    public Observable<Temperature> temperatureStream() {
        return dataStream;
    }

}
