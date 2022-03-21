package cn.locusc.reactive.other.sse.service;

import cn.locusc.reactive.other.sse.entity.LoanProcesses;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class LoanProcessesComponent {

    private final Random random = new Random();

    /**
     * 创建响应式数据流
     */
    private final Observable<LoanProcesses> loanProcessesDataStream =
            Observable.range(0, Integer.MAX_VALUE)
                    // 类似于flatMap, concatMap为有序数据集
                    .concatMap(tick ->
                            // 每一条数据生成一个响应式数据流
                            Observable.just(tick)
                                    // 显示发布事件
                                    .delay(random.nextInt(5000), TimeUnit.MILLISECONDS)
                                    // 设置背压缓冲大小
                                    .onBackpressureBuffer(100)
                                    // 获取下一条数据
                                    .doOnNext(next -> System.out.println(Thread.currentThread().getName()))
                                    // 构造推送消息
                                    .map(m -> this.loanProcessesMessage())
                                    // .share()
                                    // 热发布
                                    .publish()
                                    // 直到最后一个观察者完成才断开与下层可连接Observable的连接,RefCount跟踪有多少个观察者订阅它
                                    .refCount()
                    );

    private LoanProcesses loanProcessesMessage() {
        String message = String.format("当前订单流程结束, %s", (UUID.randomUUID()));
        return new LoanProcesses(message);
    }

    public Observable<LoanProcesses> loanProcessesObservable() {
        return loanProcessesDataStream;
    }

}
