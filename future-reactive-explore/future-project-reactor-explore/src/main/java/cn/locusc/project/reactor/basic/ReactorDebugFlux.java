package cn.locusc.project.reactor.basic;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;

/**
 * @author Jay
 * 调试响应式流
 * 2022/3/18
 */
public class ReactorDebugFlux {

    /**
     * 通过hook函数调试
     */
    @Test
    public void test1() {
        // 设置显示详细的错误堆栈信息
        Hooks.onOperatorDebug();

        Flux.range(1, 3)
                .map(item -> "item-" + item)
                .concatWith(Flux.error(new RuntimeException("手动异常")))
                .subscribe(System.out::println);
    }

    /**
     * 通过log函数调试
     */
    @Test
    public void test2() {
        Flux.range(1, 10)
                .map(item -> "item-" + item)
                .concatWith(Flux.error(new RuntimeException("手动异常")))
                .log()
                .subscribe(System.out::println);
    }
}
