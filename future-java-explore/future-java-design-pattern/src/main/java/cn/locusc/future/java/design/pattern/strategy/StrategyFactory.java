package cn.locusc.future.java.design.pattern.strategy;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jay
 * 策略工厂
 * 2022/10/14
 */
@Slf4j
public class StrategyFactory<T, S extends Strategy<T>> implements InitializingBean, ApplicationContextAware {

    private final Class<S> strategyType;

    private Map<T, S> strategyMap;

    private ApplicationContext appContext;

    /**
     * 创建一个策略工厂
     *
     * @param strategyType 策略的类型
     */
    public StrategyFactory(Class<S> strategyType) {
        this.strategyType = strategyType;
    }

    /**
     * 根据策略 id 获得对应的策略的 Bean
     *
     * @param id 策略 id
     * @return 策略的 Bean
     */
    public S getStrategy(T id) {
        return strategyMap.get(id);
    }

    @Override
    public void afterPropertiesSet() {
        // 获取Spring容器中, 所有S类型的Bean
        Collection<S> strategies = appContext.getBeansOfType(strategyType).values();

        // log.info("策略接口【{}】的实现Exec执行器类个数【{}】", strategyType.getSimpleName(), strategies.size());

        strategyMap = Maps.newHashMapWithExpectedSize(strategies.size());

        // 将所有S类型的Bean放入到strategyMap中
        for (final S strategy : strategies) {
            T id = strategy.getStrategyId();

            strategyMap.put(id, strategy);
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

}
