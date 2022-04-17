package cn.locusc.project.reactor.jpa.crud.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author Jay
 * config
 * 2022/3/20
 */
@Configuration
public class ReactivePersistenceConfiguration {

    /**
     * 根据JVM可用的处理器数量初始化一个异步边界
     * @return reactor.core.scheduler.Scheduler
     */
    @Bean
    public Scheduler jpaReactiveScheduler() {
        return Schedulers.newParallel("data-jpa-reactive", Runtime.getRuntime().availableProcessors());
    }

}
