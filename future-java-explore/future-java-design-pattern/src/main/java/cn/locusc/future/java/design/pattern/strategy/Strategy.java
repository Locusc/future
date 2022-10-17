package cn.locusc.future.java.design.pattern.strategy;

/**
 * @author Jay
 * 策略接口
 * 2022/10/14
 */
public interface Strategy<T> {

    /**
     * 获得策略的标识
     */
    T getStrategyId();

}
