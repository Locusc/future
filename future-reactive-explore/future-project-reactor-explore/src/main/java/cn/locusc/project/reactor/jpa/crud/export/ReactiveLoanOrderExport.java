package cn.locusc.project.reactor.jpa.crud.export;

import cn.locusc.project.reactor.jpa.crud.configuration.ReactivePersistenceConfiguration;
import cn.locusc.project.reactor.jpa.crud.entity.LoanOrder;
import cn.locusc.project.reactor.jpa.crud.repository.ReactiveLoanOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Component
@Import({ ReactivePersistenceConfiguration.class })
public class ReactiveLoanOrderExport implements CommandLineRunner {

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // 创建一个订单数据响应式数据流
        Flux<LoanOrder> loanOrderFlux = this.createLoanOrder().get();

        // 保存所有订单数据
        reactiveLoanOrderRepository.saveAll(loanOrderFlux)
                .count()
                .doOnNext(next -> log.info("{} orders saved in db", next))
                // block until finished
                .block();

        // 查询所有的订单数据
        Flux<LoanOrder> orders = reactiveLoanOrderRepository.findAll();
        orderResults("all orders in db:", orders);

        // 查询订单是否存在
        Mono<Boolean> exists = reactiveLoanOrderRepository.existsById(18);
        exists.subscribe(s -> log.info("does the order exist: {}", s));

        // 查询指定范围的订单
        Flux<LoanOrder> between = reactiveLoanOrderRepository.findOrderByIdBetween(
                Mono.just(15), Mono.just(20)
        );
        orderResults("orders with ids 15 ~ 20:", between);

        // 模糊查询
        Flux<LoanOrder> like = reactiveLoanOrderRepository.findOrderLikeType();
        orderResults("orders with type:", like);

        // 删除所有订单
        Mono<Void> delete = reactiveLoanOrderRepository.deleteAll();
        delete.subscribe(
                s -> log.info("delete all orders: {}", s),
                e -> log.info("delete all orders: {}", e.getMessage()),
                () -> log.info("delete all orders")
        );

        Mono.delay(Duration.ofSeconds(5)) .subscribe(s -> log.info("application finished successfully!"));
    }

    private enum OrderType {
        CAR_LOAN,
        EDU_LOAN,
        RENOVATION_LOAN,
        PARKING_LOAN,
        HOUSE_LOAN
    }

    @Resource
    private ReactiveLoanOrderRepository reactiveLoanOrderRepository;

    /**
     * 创建10个订单数据
     * @return java.util.function.Supplier<reactor.core.publisher.Flux<cn.hrfax.webflux.locusc.LoanOrder>>
     */
    private Supplier<Flux<LoanOrder>> createLoanOrder() {
        return () -> Flux.range(0, 10)
                .map(this::loanOrderInstance);
    }

    /**
     * 创建订单实例
     * @return cn.hrfax.webflux.locusc.LoanOrder
     */
    private LoanOrder loanOrderInstance(Integer m) {
        return new LoanOrder(
                UUID.randomUUID().toString(),
                OrderType.values()[m % 5].name(),
                m + 1,
                random.nextDouble() * 1000
        );
    }

    /**
     * 输出返回数据
     */
    private void orderResults(String message, Flux<LoanOrder> orderFlux) {
        orderFlux.map(LoanOrder::toString)
                .reduce(
                        new StringBuffer(),
                        (buffer, s) -> buffer.append(" - ")
                                .append(s)
                                .append("\n"))
                .doOnNext(buffer -> log.info(message + "\n {}", buffer))
                .subscribe();
    }

}
