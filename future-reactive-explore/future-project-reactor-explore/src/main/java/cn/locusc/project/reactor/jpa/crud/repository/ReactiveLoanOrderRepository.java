package cn.locusc.project.reactor.jpa.crud.repository;

import cn.locusc.project.reactor.jpa.crud.adapter.ReactiveCrudRepositoryAdapter;
import cn.locusc.project.reactor.jpa.crud.entity.LoanOrder;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * @author Jay
 * 自定义响应式查询接口
 * 2022/3/20
 */
@Repository
public class ReactiveLoanOrderRepository
        extends ReactiveCrudRepositoryAdapter<LoanOrder, Integer, LoanOrderJpaRepository> {

    public ReactiveLoanOrderRepository(LoanOrderJpaRepository delegate, Scheduler scheduler) {
        super(delegate, scheduler);
    }

    /**
     * 查询指定范围的订单
     * @return reactor.core.publisher.Flux<cn.locusc.project.reactor.jpa.crud.entity.LoanOrder>
     */
    public Flux<LoanOrder> findOrderByIdBetween(Publisher<Integer> lowerPublisher, Publisher<Integer> upperPublisher) {
        return Mono.zip(Mono.from(lowerPublisher), Mono.from(upperPublisher))
                .flatMapMany(f -> Flux
                        .fromIterable(delegate.findOrdersByIdBetween(f.getT1(), f.getT2()))
                        .subscribeOn(scheduler))
                .subscribeOn(scheduler);
    }

    /**
     * 模糊查询
     * @return reactor.core.publisher.Flux<cn.locusc.project.reactor.jpa.crud.entity.LoanOrder>
     */
    public Flux<LoanOrder> findOrderLikeType() {
        return Mono.fromCallable(delegate::findOrderLikeType)
                .subscribeOn(scheduler)
                .flatMapMany(Flux::fromIterable);
    }

}
